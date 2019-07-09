#!/bin/bash 
tester_dir=""
work_space=""
student_project_dir=""
test_dir_name=""
test_dir=""
classes_dir=/target/classes
olddir=""
junit_dir="src/test/java/bgu/spl"
args=""
GROUP_ID=""
FOLDER_STRUCTURE=""
MISSING_POM=""
LACKING_POM=""
JUNIT_IN_POM=""
PROJECT_COMPILATION=""
TEST_COMPILATION=""
TEST1=""
testJunit1=""

function execWIthTimout(){
	echo
	echo "Your program will receive the following arguments" 
	echo $1 $2 $3 $4 $5 $6
	echo
	mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.BookStoreRunner" -Dexec.args="$1 $2 $3 $4 $5 $6"
}

#test runner function per json file
#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
runTestsForJsonFile(){
	dir=$1
	json_file_path=$2
	GROUP_ID=${dir%assignment2}
	GROUP_ID=${GROUP_ID//A/}
	filename=${json_file_path##*/}
	test_num=${filename%%.*}
	test_dir_name='test_'$test_num
	test_dir=$student_project_dir/test_results/$test_dir_name
	args="$json_file_path $test_dir/customers.obj $test_dir/inventory.obj $test_dir/orders.obj $test_dir/moneyReg.obj $test_num"
	mkdir $test_dir
	echo "#########################    Running student project to create serialized output file number $test_num using $json_file_path"
	tester_classes_dir=$tester_dir$classes_dir
	#Run the student's project. need to check that he doesn't get stuck in a deadlock. 
	export -f execWIthTimout
	timeout 80s bash -c "execWIthTimout $args"
	# Copy the student's "bgu" class's folder into our target/classes folder.
	cd $tester_classes_dir
	mkdir bgu; cd bgu
	cp -R $student_project_dir$classes_dir/bgu/* ./
	cd ../../..
	# Run the tests for the current student
	echo "#########################    Running test $test_num ..."
	mvn compile; mvn exec:java -Dexec.mainClass="testRunner.Tester" -Dexec.args="$args"
	eval TEST$test_num="0"
	success_string="Nice!! All tests passed!"
	#check first if student's project timed out, and then the result file was not created
	if [ ! -f test_result.txt ];then
		echo -e "#########################    Your test failed! probably due to time out (but can also be caused by other issues. )\n" > test_result.txt
	fi
	firstline=$(head -n1 test_result.txt)
	if [[ $firstline == *"$success_string"* ]] ; then
		eval TEST$test_num="1"
	else
		echo "#########################    You have errors in TEST$test_num! The first line of the your log error file (can be found in test results folder) is:"
	fi
	echo $firstline;
	echo ""
	echo ""
	cp test_result.txt $test_dir/
	rm -rf test_result.txt
  	cd $student_project_dir

}
#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

print_header(){
	echo ""
	echo ""
	echo "********************************************************"
	echo "********************************************************"
	echo "********************************************************"
	echo "***                                              *******"
	echo "***                Running $1                 *******"
	echo "***                                              *******"
	echo "********************************************************"
	echo "********************************************************"
	echo "********************************************************"
}

test_project_settings(){
	#Some students might have saved their project inside the wrong folder. need to extract the contents to this location.
	#Check if students did not set the source for java 1.8. 
	FOLDER_STRUCTURE="1"
	MISSING_POM="1"
	LACKING_POM="1"
	JUNIT_IN_POM="1"
	if [ ! -d "src" ]; then
		FOLDER_STRUCTURE="0"
		echo "You placed your project src wrong location!"
		from=$PWD
		cd -- "$(find . -name pom.xml -type f -printf '%h' -quit)" #find the pom.xml and CD to that location.
		toCopy=$PWD
		cd $from
		rsync -r $toCopy/ $PWD
	fi
	#check if pom file exists in folder
	if [ ! -f pom.xml ];then
		MISSING_POM="0"
	fi
	#check if student even has a junit dependecy
	grep -q "<groupId>junit" pom.xml
	if [ ! $? -eq 0 ]; then
		echo "project does not support junit!"
		JUNIT_IN_POM="0"
	fi
}

set_unittest_flags(){
	testJunit1=$1
}

run_junit_tests(){
	echo "Running junit tests"
	echo "Missing currect junit in the pom." > junit_output.txt #set as default, if tests passed it will get overwritten. 
	set_unittest_flags 0
	TEST_COMPILATION="0"
	if [ $MISSING_POM == "1" ]; then #if the student is missing the pom in the root dir he will get zero in the unit tests, so no need to run unittests for him..
		rm -rf $junit_dir/mics
		mkdir -p $junit_dir
		cp -aR $tester_dir/unit_tests/mics $junit_dir
		timeout 420 bash -c "mvn test -q > junit_output.txt"
		error_string="COMPILATION ERROR"
		error_string2="Some problems were encountered while processing the POMs"
		if [[ $(head -n1 junit_output.txt) == *"$error_string"* ]] || [[ $(head -n1 junit_output.txt) == *"$error_string2"* ]]; then
			echo 'unit-tests are getting comilation errors!'
		else
			TEST_COMPILATION="1"
			#initialize all unittest flags with 1 (=passed)
			set_unittest_flags 1
			#if exist, find all the tests which failed.
			sed -ne '/.FAILURE!/p' junit_output.txt  > junit_failures.txt
			echo "Searching for failed unit tests..."
			while read p; do
				failure="${p%%(*}"
				eval $failure="0"
			done < junit_failures.txt
		fi
	
	fi

	#erase the junit_code so wont get run in the next code compilation
	rm -rf $junit_dir/mics
}


######################################## "main" ########################################


#Run the script from inside the tester's project!

#extract all the tar.gz folders in the directory
#compile and run the student's code
tester_dir=$PWD
if [ ! -f $tester_dir/automatic_check.csv ];then
	echo "GROUP_ID,FOLDER_STRUCTURE,TEST1,UNIT_TEST1" > $tester_dir/automatic_check.csv
fi
json_files=$tester_dir"/jsonFiles/*"
cd ..
work_space=$PWD
mkdir checked_projects
mkdir backups
#assumes the students tar.gz files are in the students folder
cd student
for f in *.tar.gz
do
	#extract the tr.gz 
	dir=${f%*/}
	dir=`echo $f | cut -d'.' -f1`
	olddir=$dir
	dir=`echo $f | cut -c1-5`
	mkdir $dir
	tar zxf "$f" -C $dir
	# look for empty dir. means the students file is not in gzip format! 
	if [ ! "$(ls -A $dir)" ]; then
		tar xvf "$f" -C $dir
	fi

  	#cd to the new folder and do the following:
  	#Run the different tests using the different json files
  	#cp $json_file_path $dir/
	cd $dir
	print_header $dir
	mkdir test_results
	test_project_settings
	student_project_dir=$PWD
	#run junit tests
	run_junit_tests
	#compile the students project, if comilation fails, update flag.
	PROJECT_COMPILATION="1"
	mvn clean compile
	rc=$?
	if [[ $rc -ne 0 ]] ; then
		PROJECT_COMPILATION="0"
	  	echo 'Your project has comilation errors!'
	fi
  	for json_file in $json_files
	do
		runTestsForJsonFile $dir $json_file
	done
	#print the students test results to the csv file
	echo -e "$GROUP_ID,$FOLDER_STRUCTURE,$TEST1,$testJunit1" >> $tester_dir/automatic_check.csv
	cp $tester_dir/automatic_check.csv $work_space/backups/
	now=$(date +"%m_%d_%Y_%T")
	mv $work_space/backups/automatic_check.csv $work_space/backups/automatic_check$now.csv
	#mv $work_space/student/$olddir.tar.gz $work_space/checked_projects/

  	cd ..
done


