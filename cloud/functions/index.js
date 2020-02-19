const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.sendAlertNotification = functions.database.ref('/alerts/{alertId}')
    .onCreate((snapshot, context) => {
        const alert = snapshot.val();
        
        const payload = {
            notification: {
                title: alert.title,
                body: alert.description
            }
        };

        return admin.messaging().sendToTopic("alerts", payload)
            .then(response => {
                console.log('Notification sent:', response);
                return response;
            })
            .catch(error => {
                console.log('Error:', error);
            })
    });
