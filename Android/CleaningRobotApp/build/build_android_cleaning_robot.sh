#!/bin/bash

SCRIPT_VERSION=0.1

show_usage() {
 echo "Usage: "
 echo "  " `basename $0`"  -b <build_type> -p <product_type>"
 echo "  -h will show this message"
 echo "  -v Display version"

}

show_version() {
echo "Version: " $SCRIPT_VERSION

}

BUILD_TYPE=""
PRODUCT_TYPE=""
while [ $# -gt 0 ]
do
    case "$1" in
        -v) show_version; exit 0;;
	-h) show_usage; exit 0;;
        -b) BUILD_TYPE=$2; shift;;
        -p) PRODUCT_TYPE=$2; shift;;
	-*) echo >&2 \
	    show_usage; 
	    exit 1;;
	*)  break;;	# terminate while loop
    esac
    shift
done

APK_SUFFIX="_Beta"
PRODUCT_SUFFIX="_Vorwerk"
#Get the build number from AndroidManifest.xml file
CLEANING_ROBOT_APP_BUILD_NUMBER=`perl -e 'while($line=<>) { if ($line=~ /versionName\s*=\s*"([^"]+)"/) { print "$1";}}' < ../AndroidManifest.xml`
CLEANING_ROBOT_APP_BUILD_NUMBER="${CLEANING_ROBOT_APP_BUILD_NUMBER//\./_}"
CLEANING_ROBOT_APP_OUTPUT_FILE_NAME="CleaningRobotApp_"$CLEANING_ROBOT_APP_BUILD_NUMBER$APK_SUFFIX".apk"

VORWERK_ANT_PROPERTIES_BUILD_FILE="vorwerk_ant.properties"
NEATO_ANT_PROPERTIES_BUILD_FILE="neato_ant.properties"
ANT_PROPERTIES_BACKUP_FILE="ant.properties.bak"
ANT_PROPERTIES_FILE_NAME="ant.properties"

TARGET_ANT_PROPERTIES_BUILD_FILE=$VORWERK_ANT_PROPERTIES_BUILD_FILE
TARGET_RESOURCE_TYPE="createAndroidVorwerk"
SERVER_ENVIRONMENT_FILE="environment_vorwerk_beta.xml"

shopt -s nocasematch
if [[ $PRODUCT_TYPE == "neato" ]]; then
    # createAndroidNeato has some issues and does not build. So for now using createAndroidVorwerk
	TARGET_RESOURCE_TYPE="createAndroidVorwerk"
	TARGET_ANT_PROPERTIES_BUILD_FILE=$NEATO_ANT_PROPERTIES_BUILD_FILE
        PRODUCT_SUFFIX="_Neato"
        SERVER_ENVIRONMENT_FILE="environment_neato_staging.xml" 
else
	if [[ $PRODUCT_TYPE == "vorwerk" ]]; then
		TARGET_RESOURCE_TYPE="createAndroidVorwerk"
		TARGET_ANT_PROPERTIES_BUILD_FILE=$VORWERK_ANT_PROPERTIES_BUILD_FILE
	        PRODUCT_SUFFIX="_Vorwerk"
                SERVER_ENVIRONMENT_FILE="environment_vorwerk_beta.xml"
	fi
fi

# first delete the existing server environement file and copy the new server environment
rm -f ../../SlideAndroidPlugins/res/values/environment_*
cp ../../SlideAndroidPlugins/$SERVER_ENVIRONMENT_FILE ../../SlideAndroidPlugins/res/values/$SERVER_ENVIRONMENT_FILE

CLEANING_ROBOT_APP_OUTPUT_FILE_NAME="CleaningRobotApp_"$CLEANING_ROBOT_APP_BUILD_NUMBER$APK_SUFFIX$PRODUCT_SUFFIX".apk"
#first take backup of the existing ant.properties  file
cp -f ../$ANT_PROPERTIES_FILE_NAME  ../$ANT_PROPERTIES_BACKUP_FILE
cp -f ./$TARGET_ANT_PROPERTIES_BUILD_FILE ../$ANT_PROPERTIES_FILE_NAME

ant -buildfile ../../../webresources/buildtool/build.xml $TARGET_RESOURCE_TYPE

cp -rf ../../../webresources/dist/android/www ../assets/
ant -buildfile ../build.xml clean release

mv -f ../$ANT_PROPERTIES_BACKUP_FILE ../$ANT_PROPERTIES_FILE_NAME

cp ../bin/CleaningRobotApp-release.apk ../$CLEANING_ROBOT_APP_OUTPUT_FILE_NAME
