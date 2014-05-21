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




VORWERK_BETA_BUILD_FILE="build_vorwerk_beta.xml"
VORWERK_DEV_BUILD_FILE="build_vorwerk_dev.xml"
NEATO_BETA_BUILD_FILE="build_neato_beta.xml"
NEATO_DEV_BUILD_FILE="build_neato_dev.xml"
RAJATOGO_DEMO_BUILD_FILE="build_rajatogo_demo.xml"
CLEANING_ROBOT_APP_PINFO="CleaningRobotApp-Info.plist"
CLEANING_ROBOT_APP_PINFO_BACKUP="CleaningRobotApp-Info_back.plist"
TARGET_BUILD_FILE=$VORWERK_DEV_BUILD_FILE
TARGET_RESOURCE_TYPE="createiOSVorwerk"
shopt -s nocasematch
if [[ $PRODUCT_TYPE == "neato" ]]; then
    # createiOSNeato has some issues and does not build. So for now using createiOSVorwerk
	TARGET_RESOURCE_TYPE="createiOSVorwerk"
	if [[ $BUILD_TYPE == "beta" ]]; then
		TARGET_BUILD_FILE=$NEATO_BETA_BUILD_FILE
	elif [[ $BUILD_TYPE == "dev" ]]; then
		TARGET_BUILD_FILE=$NEATO_DEV_BUILD_FILE
	fi
elif [[ $PRODUCT_TYPE == "vorwerk" ]]; then
		TARGET_RESOURCE_TYPE="createiOSVorwerk"
		if [[ $BUILD_TYPE == "beta" ]]; then
			TARGET_BUILD_FILE=$VORWERK_BETA_BUILD_FILE
		elif [[ $BUILD_TYPE == "dev" ]]; then
			TARGET_BUILD_FILE=$VORWERK_DEV_BUILD_FILE
		fi
elif [[ $PRODUCT_TYPE == "rajatogo" ]]; then
	if [[ $BUILD_TYPE == "demo" ]]; then
		echo "Building Rajatogo demo..."
		TARGET_BUILD_FILE=$RAJATOGO_DEMO_BUILD_FILE
	fi
fi

cp ./$TARGET_BUILD_FILE ../$TARGET_BUILD_FILE
#first take backup of the existing plist file
cp -f ../CleaningRobotApp/$CLEANING_ROBOT_APP_PINFO ../CleaningRobotApp/$CLEANING_ROBOT_APP_PINFO_BACKUP
cp -f ./$CLEANING_ROBOT_APP_PINFO ../CleaningRobotApp/$CLEANING_ROBOT_APP_PINFO

ant -buildfile ../../webresources/buildtool/build.xml $TARGET_RESOURCE_TYPE

ant -buildfile ../$TARGET_BUILD_FILE ipa

mv -f ../CleaningRobotApp/$CLEANING_ROBOT_APP_PINFO_BACKUP ../CleaningRobotApp/$CLEANING_ROBOT_APP_PINFO

