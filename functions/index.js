const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//


// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();
// [END import]

exports.pantheonNotifications = functions.firestore.document('/Notifications/{documentId}')
    .onCreate((snap, context) => {

      const original = snap.data();
      //console.log(original);
      const notifTitle = original.notificationTitle;
      const description = original.notificationDescription;
      console.log("Title is "+notifTitle+" Description is "+description);

      const payload = {
        notification: {
            title:notifTitle,
            body: description,
            sound: "default"
        },
    };


  console.log("Payload is: ",payload);


  return admin.messaging().sendToTopic("pushNotifications", payload);
 
});