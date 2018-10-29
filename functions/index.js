


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/followers/{followedUid}/{ToUser}`.
 * Users save their device notification tokens to `/users/{followedUid}/notificationTokens/{notificationToken}`.
 */
 
 let getDistanceFromLatLonInKm= (lat1,lon1,lat2,lon2) =>  {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1); 
  var a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ; 
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  var d = R * c; // Distance in km
  return d;
}

let deg2rad =(deg) => {
  return deg * (Math.PI/180)
}
 
 
let sendFcm  = (data, context) => {
	var ToUser = data.ToUser;
	let payload  = data.payload;
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${ToUser}/notificationTokens`).once('value');

      // Get the follower profile.
     
      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      return Promise.all([getDeviceTokensPromise]).then(results => {
        tokensSnapshot = results[0];
      
        // Check if there are any device tokens.
        if (!tokensSnapshot.hasChildren()) {
          return console.log('There are no notification tokens to send to.');
        }
        console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
       

        // Notification details.
     
		

        // Listing all tokens as an array.
        tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        return admin.messaging().sendToDevice(tokens, payload);
      }).then((response) => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      }).catch(function(error) {
		  
		console.log("From send FCM"+  error);
	  });
 }
  
exports.sendNewMessageRecived = functions.database.ref('/Message/{ToUser}')
    .onWrite((change, context) => {
      const ToUser = context.params.ToUser;
     
      // If un-follow we exit the function.
      if (!change.after.val()) {
        return console.log('User ', ToUser);
      }
      console.log('We have a new follower UID:', ToUser);

      // Get the list of device notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${ToUser}/notificationTokens`).once('value');

      // Get the follower profile.
     
      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      return Promise.all([getDeviceTokensPromise]).then(results => {
        tokensSnapshot = results[0];
      
        // Check if there are any device tokens.
        if (!tokensSnapshot.hasChildren()) {
          return console.log('There are no notification tokens to send to.');
        }
        console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
       

        // Notification details.
        const payload = {
          notification: {
            title: 'You have A new Message',
            body: `click here to see Your message`,
            
          },
			"data" : {
			"Action" : "Message"
			}
	};
		

        // Listing all tokens as an array.
        tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        return admin.messaging().sendToDevice(tokens, payload);
      }).then((response) => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      }).catch(function(error) {
		console.log(error);
	  });
	 
});


exports.sendNewEnigeFoundAnewPost = functions.database.ref('/animalDetails/{Type}/{ItemId}').
onWrite((change, context) => {
	const p =  new Promise(function(resolve, reject) {
    var data = {};
	 data.ItemID = context.params.ItemId;
	 console.log("context.params.ItemID" + context.params.ItemId);
	 data.itemData = change.after._data; 
	 var IsLost = context.params.Type==='lostAnimals';
	 data.IsLost = IsLost;
	 console.log("context.params.Type :"+  context.params.Type);
	console.log("Is Lost form Main :"+   data.IsLost);
    console.log("Is Lost form Main :"+  data);
       data.payload = {
          notification: {
            title: 'A new Post that Match your Search',
            body: `click here to see This Post`,
            
          },
			"data" : {
			"Action" : "NewPost" , 
			"isLost" : IsLost.toString()
			}
	};
	 return resolve(data);
	});
	
	const query = admin.database().ref("/SearchParams/").orderByKey().once("value");
	
	 let tokensSnapshot;
	 let data ; 
	 let tokensToSend=[];
	Promise.all([p, query]).then(function(values) { 
 
    tokensSnapshot = values[1];  
	data =values[0];
	
    tokensSnapshot.forEach((childSnapshot)=> {
		
		const itemData = data.itemData;
		
      var childData = childSnapshot.val();
	  var searchParams=  childData.serachParams;
	  var ToAdd = true; 
	 
	   ToAdd = (searchParams.lost === data.IsLost) ; 
	   
	   ToAdd = ToAdd && ((!searchParams.animalType)  || searchParams.animalType === itemData.animalType)
	    
	   ToAdd = ToAdd && ((!searchParams.fromDate)  || searchParams.fromDate.time < itemData.date)
	  
	  if(ToAdd && searchParams.selectPlace  && searchParams.radius  )
	  {
		 if(itemData.loc)
		 {
			
			var km =  getDistanceFromLatLonInKm(itemData.loc.latitude, itemData.loc.longitude, 
			searchParams.selectPlace.latitude ,searchParams.selectPlace.longitude);
			ToAdd = km < searchParams.radius+1;
		 }
		 else 
		 {
			 ToAdd = false;
		 }
	     
	  }
	  
	  if (ToAdd)
	  {
		  console.log("I need to send a message");
		  var dataToMessage={}; 
		 dataToMessage.ToUser = childData.userId;
		 dataToMessage.payload = data.payload;
		 	  tokensToSend.push(sendFcm(dataToMessage,context));
	  }
	   
	}); 
	
	return Promise.all(tokensToSend);
	
}).catch(function(error) {
	  console.log("from DB");
	  console.log(error);
	  });
	  return true;
	
	});
