# Google Play Console Data Safety Questionnaire: Exact Form Answers

Use this guide to complete the **Data Safety Section** in the Google Play Console for **LifeScore**.

---

## 🔒 Overview Questions

1. **Does your app collect or share any of the required user data types?**
   * 👉 **YES**
2. **Is all of the user data collected by your app encrypted in transit?**
   * 👉 **YES** (All network traffic uses TLS 1.3 / HTTPS).
3. **Do you provide a way for users to request that their data is deleted?**
   * 👉 **YES** (Users can tap "Delete My Account" in Settings or email privacy@[YOUR_WEBSITE]).
   * **URL for deletion request:** `https://[YOUR_WEBSITE]/privacy_policy.html#deletion`

---

## 📊 Data Types & Collection Questionnaire Matrix

### 1. Personal Info
* **Name**
  * Collected: **Yes** | Shared: **No**
  * Required: **Optional / App Functionality**
  * Ephemeral: **No** (Stored in Firestore until account deletion)
  * Purpose: Account management & personalization
* **Email address**
  * Collected: **Yes** | Shared: **No**
  * Required: **Yes** (For Firebase Authentication)
  * Ephemeral: **No**
  * Purpose: Account management & cloud synchronization
* **User IDs (Firebase UID)**
  * Collected: **Yes** | Shared: **No**
  * Required: **Yes**
  * Ephemeral: **No**
  * Purpose: Account functionality & data integrity

---

### 2. Photos, Videos & Audio
* **Photos & Videos**
  * Collected: **No** (Processed locally on-device for Micro-Vlog stitching, not transmitted to external cloud servers).
  * Shared: **No**
* **Voice or Sound Recordings**
  * Collected: **No** (Processed on-device only).
  * Shared: **No**

---

### 3. App Activity
* **App Interactions (Habit completions, score milestones)**
  * Collected: **Yes** | Shared: **No**
  * Required: **Yes**
  * Ephemeral: **No**
  * Purpose: Calculating 8-Dimension LifeScore index, maintaining streaks, and AI coaching.
* **In-app search history**
  * Collected: **No**

---

### 4. Financial Info
* **User payment info**
  * Collected: **No** (All transactions are handled directly by Google Play In-App Billing; LifeScore never sees credit card numbers).
* **Purchase history (Subscription SKU & active tier)**
  * Collected: **Yes** | Shared: **No**
  * Required: **Yes**
  * Purpose: Unlocking Pro entitlements & Reward Store coins.

---

### 5. Device or other IDs
* **Device IDs / Advertising IDs**
  * Collected: **Yes** (Firebase Installation ID for push notifications and sync triggers) | Shared: **No**
  * Required: **Yes**
  * Ephemeral: **No**
  * Purpose: Cloud sync, notifications, crash diagnostics.

---

## 🛡️ Third-Party Service Disclosure
* **Google Firebase**: Data is transferred securely to Google Cloud servers (Firestore, Firebase Auth, Crashlytics).
* **Google Gemini API**: Anonymized prompt strings are processed to generate AI coaching advice.
