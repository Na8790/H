const functions = require("firebase-functions");
const admin = require("firebase-admin");
const express = require("express");
const cors = require("cors");

admin.initializeApp();
const db = admin.firestore();
const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

// middleware: verify Firebase ID token and check role admin
async function verifyAdmin(req, res, next) {
  const authHeader = req.headers.authorization || "";
  const idToken = authHeader.startsWith("Bearer ") ? authHeader.split("Bearer ")[1] : null;
  if (!idToken) return res.status(401).send("Missing or invalid Authorization header");

  try {
    const decoded = await admin.auth().verifyIdToken(idToken);
    const userDoc = await db.collection("users").doc(decoded.uid).get();
    if (!userDoc.exists) return res.status(403).send("User record not found");
    const role = userDoc.data().role || "user";
    if (role !== "admin") return res.status(403).send("Not an admin");
    req.uid = decoded.uid;
    next();
  } catch (e) {
    console.error("verifyAdmin error:", e);
    return res.status(401).send("Token verification failed");
  }
}

// Protected: bulk disable users
app.post("/bulkDisable", verifyAdmin, async (req, res) => {
  const uids = Array.isArray(req.body.uids) ? req.body.uids : [];
  if (uids.length === 0) return res.status(400).send("uids array required");
  const batch = db.batch();
  uids.forEach(uid => {
    const ref = db.collection("users").doc(uid);
    batch.update(ref, { isActive: false });
  });
  await batch.commit();
  return res.send({ ok: true, disabled: uids.length });
});

// Protected: set a user to admin (admin only)
app.post("/setAdmin", verifyAdmin, async (req, res) => {
  const { uid } = req.body;
  if (!uid) return res.status(400).send("uid required");
  await db.collection("users").doc(uid).update({ role: "admin" });
  return res.send({ ok: true, uid });
});

// Example: get list of users (limited)
app.get("/listUsers", verifyAdmin, async (req, res) => {
  const snapshot = await db.collection("users").limit(200).get();
  const users = snapshot.docs.map(d => ({ uid: d.id, ...d.data() }));
  return res.send({ ok: true, users });
});

exports.api = functions.https.onRequest(app);
