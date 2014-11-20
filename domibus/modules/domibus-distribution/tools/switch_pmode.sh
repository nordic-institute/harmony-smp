#!/bin/sh
# --create_cron [cronexpression] [source_dir] [target_dir]
# --delete_cron [cronexpression] [source_dir] [target_dir]
# --test [source_dir] [target_dir]
# --execute [source_dir] [target_dir]


function test_pmodedir {
echo "$MY_PATH"
    #test if the current user has write access to the files/directories
    if [ ! -w "$2" ]
    then
        echo "ERROR: $2 is not writable"
    fi

    if [ ! -w "$3" ]
    then
        echo "ERROR: $3 is not writable"
        exit 0
    fi

    #test if both are directories
    if [ -d "$2" ] && [ -d "$3" ]
    then
        echo "both are directories"

        local COUNTER=0
        declare -a content_source=()
        local permission_failure_source=0
        #test if a file or directory inside the source_dir is not writable
        for current_file in `find $2`;
        do
                if [ ! -w "$current_file" ]
                then
                    permission_failure_source=1
                    echo "ERROR: $current_file is not writable"

                fi

                if [ $current_file != $2 ]
                then
                    content_source[COUNTER]=${current_file#${2}/}
                    COUNTER=$[$COUNTER +1]
                fi
        done


        if [ "$permission_failure_source" = 1 ]
        then
            echo "ERROR: please check write permissions in the source directory [$2] for all files mentioned above"
        fi


        COUNTER=0
        declare -a content_target=()
        local permission_failure_target=0
        #test if a file or directory inside the target_dir is not writable
        for current_file in `find $3`;
        do
                if [ ! -w "$current_file" ]
                then
                    permission_failure_target=1
                    echo "ERROR: $current_file is not writable"
                fi

                #dont write the filepath into the array
                if [ $current_file != $3 ]
                then
                    content_target[COUNTER]=${current_file#${3}/}
                    COUNTER=$[$COUNTER +1]
                fi
        done

        if [ "$permission_failure_target" = 1 ]
        then
            echo "ERROR: please check write permissions in the target directory [$3] for all files mentioned above"
            exit 0
        fi



        #test if the content (number of files) of the source and target folder are different
        local equals=0
        [ "${content_source[*]}" == "${content_target[*]}" ] && equals=1 || equals=0

        #if equals = 0 the test was not successful and the user has to confirm that the files should be copied
        if [ "$equals" = 0 ]
        then
            if [ "$4" != "-f" ]
            then
                echo "ERROR: the directories do not contain the same files/directories. Please confirm that you want to copy the files. To do this use: -f as the last parameter for create_cron"
                exit 0
            fi
        fi


        echo "INFO: Test successful"


    else
        #test if both are files
        if [ -f "$2" ] && [ -f "$3" ]
        then
            echo "both are files"
        else
            #if both are NOT of the same type
            echo "ERROR: please verify that [source_dir] and [target_dir] are both of the same type (files or directories)."
            exit 0
        fi
    fi
}

function create_cron {
    echo "command: $1 source_dir:$2 target_dir:$3 month:$4 day:$5 hour:$6 minute:$7 forced: $8 "

    test_pmodedir create_cron $2 $3 $8

    #write out current crontab
    crontab -l > mycron
    #echo new cron into cron file
    echo "$7 $6 $5 $4 * $(readlink -f $0) --execute $(readlink -f $2) $(readlink -f $3) $8" >> mycron
    #install new cron file
    crontab mycron
    rm mycron
}

case "$1" in
    "--create_cron")
        create_cron $@
        ;;
    "--delete_cron")
        echo "2";
        ;;
    "--test")
        test_pmodedir $@
        ;;
    "--execute")
        test_pmodedir execute $2 $3 $4 
	cp -rf $2 $3
	# delete cronjob
        ;;
    *)
        echo -e "valid parameters are: \n--create_cron [source_dir] [target_dir] [month] [day] [hour] [minute] {[-f] optional}\n--test [source_dir] [target_dir]"
        ;;
    esac



