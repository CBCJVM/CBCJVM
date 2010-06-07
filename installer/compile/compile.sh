#!/bin/sh

DIR=/mnt/user/bin
PROG=$DIR/robot
NAME=$DIR/robot_name.txt

rm -rf $DIR
mkdir -p $DIR

echo "Program last modified:"
date -R -r ${1}

if [ ${1##*.} = "bin" ]; then
  echo "Copying ${1}" 
  cp ${1} $PROG
  if [ -e $PROG ]; then
    echo "${1}" > $NAME
    echo "Copy succeeded."
	/mnt/user/nhs/success_sound &
  else
    echo "Copy failed."
	/mnt/user/nhs/fail_sound &
  fi
else
  
  if [ ${1##*.} = "wav" ]; then
    echo "Playing audio file ${1}" 
    btplay ${1}
  
  else

    if [ ${1##*.} = "mp3" ]; then
      echo "Playing audio file ${1}" 
      btplay ${1}
    else
      
      if [ ! -e "${1%.*}.bin" ]; then
      
        echo "Compiling ${1}"
        if [ ${1##*.} = "c" ]; then
          /mnt/kiss/usercode/gcc-usercode "${1}" -o ${1%.*}.bin 2>&1
        else 
          if [ ${1##*.} = "cpp" ]; then
            echo "C++ is coming soon."
          else
            if [ ${1##*.} = "cxx" ]; then
              echo "C++ is coming soon."
            else
				if [ ${1##*.} = "class" ]; then
	              	echo "Copying proxy to ${DIR}"
					FOLDER=${1%/*}
					FILE=${1##*/}
					LOL=${FILE%%.*}
					echo "#!/bin/sh
					cd ${FOLDER}
					/mnt/user/jvm/java ${LOL}" > ${1%.*}.bin
					echo "${LOL}" > $NAME
	            else
              		echo "Unsupported format.  Only C, C++, Java, WAV, MP3, and binary are supported."
				fi
            fi
          fi
        fi
        if [ -e ${1%.*}.bin ]; then
          echo "Compile succeeded." 
          
	      echo "Copying ${1%.*}.bin"
	      cp ${1%.*}.bin $PROG
	      if [ -e $PROG ]; then
            echo "Copy succeeded."
  	        /mnt/user/nhs/success_sound &
  	        echo "${1}" > $NAME
          else
            echo "Copy failed."
	        /mnt/user/nhs/fail_sound &
          fi
        else
          echo "Compile failed."
	      /mnt/user/nhs/fail_sound &
        fi
      
      else
        echo "Copying ${1%.*}.bin"
          cp ${1%.*}.bin $PROG
          if [ -e $PROG ]; then
              echo "Copy succeeded."
  	        /mnt/user/nhs/success_sound &
  	        echo "${1}" > $NAME
	      else
            echo "Copy failed."
	        /mnt/user/nhs/fail_sound &
          fi
      fi
    fi  
  fi
fi

chmod -R 777 /mnt/user/

sync                                                                                                
sync