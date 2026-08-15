# Google Play Console Launch & Track Rollout Guide

## 📋 Release Tracks Strategy

### Track 1: Internal Testing Track (Day 1)
* **Audience**: Core team and QA (Up to 100 internal email addresses).
* **URL Generation**: Instant access without Google review waiting time.
* **Goal**: Verify live Google Play Billing sandbox purchases, CameraX permissions, and Firestore offline sync on real hardware devices.

### Track 2: Closed Alpha Track (Days 2 - 5)
* **Audience**: 500 invite-only beta community users.
* **Goal**: Validate psychometric assessment completion rate, viral meme share engagement, and 7-day streak retention.

### Track 3: Production Track (Day 7+)
* **Staged Rollout Schedule**:
  * **Day 1**: 10% of users (Monitor crash rates on Crashlytics)
  * **Day 2**: 25% of users
  * **Day 3**: 50% of users
  * **Day 5**: 100% Full Global Availability

---

## 🛡️ Play Console Data Safety Questionnaire Answers

| Data Type | Collected? | Shared? | Purpose | Ephemeral? |
|---|---|---|---|---|
| **Name & Email** | Yes | No | Account management & Cloud Sync | No |
| **User IDs (UID)** | Yes | No | Cloud Firestore storage & synchronization | No |
| **Health & Fitness** | Yes | No | LifeScore vitality index calculation | No |
| **In-App Purchase History** | Yes | No | Pro subscription verification | No |
| **Photos & Videos** | Yes (Local Only) | No | Micro-Vlogs stored in private sandbox | Stored locally |
| **App Performance & Crash Logs** | Yes | No | App analytics & bug fixes | No |

* **Data Encryption**: All data is encrypted in transit using TLS 1.3 / HTTPS.
* **Data Deletion Mechanism**: Users can delete their account and data within the app under `Settings > Delete Account`.

---

## 📦 Release Artifact Location
* **App Bundle (.aab)**: `app/build/outputs/bundle/release/app-release.aab`
* **Version Code**: `1`
* **Version Name**: `1.0.0`
* **Min SDK**: `26` (Android 8.0 Oreo)
* **Target SDK**: `34` (Android 14)
