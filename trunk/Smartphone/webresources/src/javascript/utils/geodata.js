// grid starts at top left with x:0 y:0
// return geography structure for floor1 or floor2 
function getGeogophy(floorId) {
var geo = {
    "floor1": {
        // internal unique floor identifier for the robot can't, be changed from user
        id:"floor1", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
        // user defined floor name, can be changed in UI ? default is internal name
        name:"first floor",
        // the max floor size as bounding box: minX, minY, maxX, maxY
        boundingBox:[0,0,800,800],
        // collection of all rooms for this floor (Room Maps)
        rooms:[
        {
            // internal unique room identifier for the robot can't, be changed from user
            id:"room1", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
            // user defined room name, can be changed in UI. default is internal name
            name:"room1",
            // user defined icon for the room, can be changed in UI. default ICON.EMPTY (no icon)
            icon:ICON.EMPTY,
            // background color will be generated according selected color (e.g. set alpha 50%)
            // could also be generated on client side
            color:COLOR.BLUE,
            // selected background color can be changed from user
            //selColor:"#FFFF00",
            // bounding box for the room: minX, minY, maxX, maxY
            boundingBox:[5,5,57,55],
            // polygon for the room, each entry represents the next point to draw to
            coord:[
                {x:5,y:5},
                {x:57,y:5},
                {x:57,y:55},
                {x:5,y:55}
            ]
        },
        {
            // internal unique room identifier for the robot can't, be changed from user
            id:"room2", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
            // user defined room name which can be changed in UI. default is internal name
            name:"My Kitchen",
            // user defined icon for the room, can be changed in UI. default ICON.EMPTY (no icon)
            icon:ICON.KITCHEN,
            // background color will be generated according seleceted color (e.g. set alpha 50%)
            color:COLOR.PURPLE,
            // selected background color can be changed from user
            //selColor:"#FF0000",
            // bounding box for the room: minX, minY, maxX, maxY
            boundingBox:[60,20,180,150],
            // polygon for the room, each entry represents the next point to draw to
            coord:[
                {x:60,y:20},
                {x:180,y:20},
                {x:180,y:50},
                {x:160,y:80},
                {x:160,y:50},
                {x:100,y:50},
                {x:100,y:100},
                {x:180,y:130},
                {x:180,y:150},
                {x:60,y:150}
            ]
        },
        {
            id:"room3",
            name:"room3",
            icon:ICON.EMPTY,
            color:COLOR.GREEN,
            //selColor:"#00FF00",
            boundingBox:[160,20,390,390],
            coord:[
                {x:183,y:20},
                {x:390,y:20},
                {x:390,y:390},
                {x:183,y:390},
                {x:183,y:250},
                {x:200,y:250},
                {x:200,y:180},
                {x:183,y:180},
                {x:183,y:120},
                {x:160,y:110},
                {x:160,y:90},
                {x:183,y:80}
            ]
        },
        {
            id:"room4",
            name:"room4",
            icon:ICON.EMPTY,
            color:COLOR.ORANGE,
            //selColor:"#FF0000",
            boundingBox:[5,57,57,107],
            coord:[
                {x:5,y:57},
                {x:57,y:57},
                {x:57,y:107},
                {x:5,y:107}
            ]
        },
        {
            id:"room5",
            name:"room5",
            icon:ICON.EMPTY,
            color:COLOR.YELLOW,
            //selColor:"#FF0000",
            boundingBox:[5,110,180,257],
            coord:[
                {x:5,y:110},
                {x:57,y:110},
                {x:57,y:153},
                {x:180,y:153},
                {x:180,y:257},
                {x:5,y:257}
            ]
        },
        {
            id:"room6",
            name:"room6",
            icon:ICON.EMPTY,
            color:COLOR.GREY,
            //selColor:"#FF0000",
            boundingBox:[5,260,90,390],
            coord:[
                {x:5,y:260},
                {x:90,y:260},
                {x:90,y:390},
                {x:45,y:390}
            ]
        },
        {
            id:"room7",
            name:"room7",
            icon:ICON.EMPTY,
            color:COLOR.RED,
            //selColor:"#FF0000",
            boundingBox:[103,260,180,390],
            coord:[
                {x:103,y:260},
                {x:180,y:260},
                {x:180,y:390},
                {x:93,y:390},
                {x:93,y:270}
            ]
        },
        {
            id:"room8",
            name:"room8",
            icon:ICON.EMPTY,
            color:COLOR.BLACK,
            //selColor:"#FF0000",
            boundingBox:[405,405,457,455],
            coord:[
                {x:405,y:405},
                {x:457,y:405},
                {x:457,y:455},
                {x:405,y:455}
            ]
        },
        {
            id:"room9",
            name:"room9",
            icon:ICON.EMPTY,
            color:COLOR.CYAN,
            //selColor:"#FF00FF",
            boundingBox:[183,20,390,390],
            coord:[
                {x:460,y:420},
                {x:580,y:420},
                {x:580,y:450},
                {x:560,y:480},
                {x:560,y:450},
                {x:500,y:450},
                {x:500,y:500},
                {x:580,y:530},
                {x:580,y:550},
                {x:460,y:550}
            ]
        },
        {
            id:"room3",
            name:"room3",
            icon:ICON.EMPTY,
            color:COLOR.ORANGE,
            //selColor:"#00FF00",
            boundingBox:[583,420,790,790],
            coord:[
                {x:583,y:420},
                {x:790,y:420},
                {x:790,y:790},
                {x:583,y:790},
                {x:583,y:650},
                {x:600,y:650},
                {x:600,y:580},
                {x:583,y:580},
                {x:583,y:520},
                {x:560,y:510},
                {x:560,y:490},
                {x:583,y:480}
            ]
        },
        {
            id:"room4",
            name:"room4",
            icon:ICON.EMPTY,
            color:COLOR.PURPLE,
            //selColor:"#FF0000",
            boundingBox:[405,457,457,507],
            coord:[
                {x:405,y:457},
                {x:457,y:457},
                {x:457,y:507},
                {x:405,y:507}
            ]
        },
         {
            id:"room5",
            name:"room5",
            icon:ICON.EMPTY,
            color:COLOR.GREEN,
            //selColor:"#FF0000",
            boundingBox:[405,510,580,657],
            coord:[
                {x:405,y:510},
                {x:457,y:510},
                {x:457,y:553},
                {x:580,y:553},
                {x:580,y:657},
                {x:405,y:657}
            ]
        },
         {
            id:"room6",
            name:"room6",
            icon:ICON.EMPTY,
            color:COLOR.RED,
            //selColor:"#FF0000",
            boundingBox:[405,660,490,790],
            coord:[
                {x:405,y:660},
                {x:490,y:660},
                {x:490,y:790},
                {x:445,y:790}
            ]
        },
        {
            id:"room7",
            name:"room7",
            icon:ICON.EMPTY,
            color:COLOR.CYAN,
            //selColor:"#FF0000",
            boundingBox:[503,660,580,790],
            coord:[
                {x:503,y:660},
                {x:580,y:660},
                {x:580,y:790},
                {x:493,y:790},
                {x:493,y:670}
            ]
        }],
        // collection of all no-go areas for this floor (No-go Maps)
        noGo:[
            // bounding box for each area: minX, minY, maxX, maxY
            [120,30,150,45],
            [65,110,85,140]
        ],
        // collection of all base stations for this floor (Base Station Map)
        base:[
            // bounding box for each station: minX, minY, maxX, maxY
            [20,5,25,10]
            // could also be just a point to place an icon: x, y
            //[20,5],
        ]
    },
    "floor2":{
        // internal unique floor identifier for the robot can't, be changed from user
        id:"floor2", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
        // user defined floor name, can be changed in UI ? default is internal name
        name:"second floor",
        // the max floor size as bounding box: minX, minY, maxX, maxY
        boundingBox:[0,0,190,160],
        // collection of all rooms for this floor (Room Maps)
        rooms:[
        {
            // internal unique room identifier for the robot can't, be changed from user
            id:"room1", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
            // user defined room name, can be changed in UI. default is internal name
            name:"room1",
            // user defined icon for the room, can be changed in UI. default ICON.EMPTY (no icon)
            icon:ICON.EMPTY,
            // background color will be generated according selected color (e.g. set alpha 50%)
            // could also be generated on client side
            color:"#FFFF80",
            // selected background color can be changed from user
            selColor:"#FFFF00",
            // bounding box for the room: minX, minY, maxX, maxY
            boundingBox:[5,5,57,55],
            // polygon for the room, each entry represents the next point to draw to
            coord:[
                {x:5,y:5},
                {x:57,y:5},
                {x:57,y:55},
                {x:5,y:55}
            ]
        },
        {
            // internal unique room identifier for the robot can't, be changed from user
            id:"room2", // could also be something like "a696f73e5-2bf7-4653-ab91-12473faac4c8"
            // user defined room name which can be changed in UI. default is internal name
            name:"My Kitchen",
            // user defined icon for the room, can be changed in UI. default ICON.EMPTY (no icon)
            icon:ICON.KITCHEN,
            // background color will be generated according seleceted color (e.g. set alpha 50%)
            color:"#FF8080",
            // selected background color can be changed from user
            selColor:"#FF0000",
            // bounding box for the room: minX, minY, maxX, maxY
            boundingBox:[60,20,180,150],
            // polygon for the room, each entry represents the next point to draw to
            coord:[
                {x:60,y:20},
                {x:180,y:20},
                {x:180,y:50},
                {x:160,y:80},
                {x:160,y:50},
                {x:100,y:50},
                {x:100,y:100},
                {x:180,y:130},
                {x:180,y:150},
                {x:60,y:150}
            ]
        }],
        // collection of all no-go areas for this floor (No-go Maps)
        noGo:[
            // bounding box for each area: minX, minY, maxX, maxY
            [120,30,150,45],
            [65,110,85,140]
        ],
        // collection of all base stations for this floor (Base Station Map)
        base:[
            // bounding box for each station: minX, minY, maxX, maxY
            [20,5,25,10]
            // could also be just a point to place an icon: x, y
            //[20,5],
        ]
    }
}

return geo[floorId] ? geo[floorId] : null;
}
