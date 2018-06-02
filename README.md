# RefineMobile
A Demo Mobile app for X-Ray Refine

## Project Structure
A somewhat typical app structure, there are 3 main activities at the moment, as well as some supporting Layouts. There are also additional Java Classes used to provide some structure to the app, including an ```AppDataModel``` and ```XRayJsonReader```.

**Main Activities/Views.**

```MainActivity```: is where the app goes to when it has finished loading and retrieving app data from the phone and XRay API.

```ListApplications```: Is an intermediate activity that provides a searchable list of all apps installed on the user's Phone.

```AppDetailView```: An activity used to provide a detailed view on the selected app, including usage stats and host information.

**Supporting Activities/Views**

```splash_screen```: A layout used as a splash screen with the ```MainActivity```.

```nav_header```/```drawer_menu```: Layouts used with ```MainActivity``` to form a nav menu

**Additional Classes**

```AppDataModel```: Singleton class that holds all app data used throughout the app. Primarily consists of 3 ```HashMap``` objects, each using an App's Package Name as the key. The 3 hash maps are ```xRayApps```, ```allPhoneAppInfos```, ```trackedPhoneAppInfos```

```XRayJsonReader```: The XRay App observatory is implemented as a REST API only supporting JSON. This JsonReader provides methods for parsing a Json response from an API Query.

```AppInfo```: A soon to be Deprecated class used to hold app info retrieved from the User's phone and not from the XRay API. Will be replaced with ```XRayApp```.

```XRayApp```: A data class holding app information requested from the XRay API. Uses ```XRayAppStoreInfo``` to hold the store info data requested for an app from the XRay API. Note: This class will replace the ```AppInfo``` class.


## App Screenshots
Listing of All apps installed on the user's phone:

<img src="https://raw.githubusercontent.com/AdamSlack/RefineMobile/master/reameimages/applistview.png" width=250>

Filtering list of installed apps with search:

<img src="https://raw.githubusercontent.com/AdamSlack/RefineMobile/master/reameimages/applistfilter.png" width=250>

Detail view of selected app (Total usage time for the past day):

<img src="https://raw.githubusercontent.com/AdamSlack/RefineMobile/master/reameimages/appdetailday.png" width=250>
