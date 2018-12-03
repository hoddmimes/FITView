#!/usr/bin/bash
if ![java -version 2>&1 >/dev/null | grep -q 'version "1.']; then                                                                                                       echo 'java is not installed or not available'                                                                                                                       exit 0   
fi
JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*"/\1\2/p;')
if [ "$JAVA_VER" -lt "18" ]; then
    echo 'current java version is too old, version 1.8 or higher is required'
    exit 0
fi
#
#
java -classpath ./fit.jar:./jcommon-1.0.23.jar:./jfreechart-1.0.19.jar:./FITView.jar com.hoddmimes.fitview.FITView "$@"

