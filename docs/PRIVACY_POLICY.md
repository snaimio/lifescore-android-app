# Privacy Policy for LifeScore Mobile Application

*Last Updated: August 15, 2026*  
*Effective Date: August 15, 2026*

LifeScore Technologies Inc. ("LifeScore", "we", "us", or "our") is dedicated to protecting your privacy. This Privacy Policy describes how we collect, store, process, and protect your personal information in compliance with the **General Data Protection Regulation (GDPR)**, the **California Consumer Privacy Act (CCPA)**, and the **Google Play Developer Policy**.

---

## 1. Information We Collect

### A. Information You Provide Directly
* **Account Information**: When you sign in with Google or Email, we store your unique User Identifier (UID), name, and email address.
* **Assessment & Psychometric Data**: Responses to the 130-question assessment, your assigned Hero Archetype, and career affinity scores.
* **Habit & Quest Tracking**: Daily habit entries, streak records, completion timestamps, customized rewards, and coin balances.
* **Skill Practice Logs**: Deliberate practice durations and skill categories logged in the 10,000-Hour tracker.

### B. Information Processed Locally on Your Device
* **Micro-Vlog Recordings**: Video clips captured through CameraX are stored **locally in private application storage**. No unstitched video footage is uploaded to external servers without your explicit action.

### C. Automatically Collected Technical Data
* Device model, operating system version, and crash diagnostic reports via Google Play Services.

---

## 2. How We Use Your Data

* **Core Functionality**: To calculate your real-time 8-Dimension LifeScore index, maintain active streaks, and evaluate league rankings.
* **AI Coaching & Memory**: To generate contextual habit recommendations, morning executive debriefs, and weekly performance audits using Google Gemini AI APIs.
* **Cloud Synchronization**: To provide seamless multi-device backup and offline-to-online data restoration using Google Cloud Firestore.
* **Subscription Management**: To verify in-app purchases and pro tier entitlements through Google Play Billing.

---

## 3. Third-Party Service Providers

We only share data with vetted third-party infrastructure providers necessary to operate the application:
1. **Google Firebase (Firestore & Authentication)**: Encrypted database hosting and secure authentication.
2. **Google Generative AI (Gemini)**: Anonymized context prompts for coaching advice.
3. **Google Play In-App Billing**: Secure payment processing.

We **do NOT sell, rent, or trade your personal data** to data brokers or advertising networks.

---

## 4. Your Rights Under GDPR & CCPA

You have the following statutory rights regarding your personal data:
* **Right to Access**: Request an export copy of all your personal data in standard JSON format.
* **Right to Rectification**: Correct any inaccurate profile details directly inside Settings.
* **Right to Erasure (Right to Be Forgotten)**: Request complete deletion of your account and all associated Firestore documents.
* **Right to Withdraw Consent**: Toggle off cloud sync to switch to local-only SQLite mode.

---

## 5. Account & Data Deletion Instructions

To permanently delete your LifeScore account and all stored data:
1. Open the LifeScore app and navigate to **Settings ⚙️**.
2. Scroll to the **Data Management & Privacy** section.
3. Tap **"Delete Account & Wipe Cloud Data"**.
4. Confirm the prompt. All Firestore collections (`/users/{uid}`, `/tasks`, `/dimensions`) and local Room databases will be permanently destroyed within 24 hours.

Alternatively, you may submit a deletion request via email to: **privacy@lifescore.app** with the subject line *"Data Erasure Request"*.

---

## 6. Contact Us

If you have any questions or data protection inquiries, please contact our Data Protection Officer (DPO) at:
* **Email**: privacy@lifescore.app
* **Address**: LifeScore Technologies Inc., 500 Howard Street, San Francisco, CA 94105
