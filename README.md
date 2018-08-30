# Fn with Oracle DB

Fn functions executing `CRUD` operations on Oracle DB. This sample uses a simple `Employee` entity for demonstration purposes

## Pre-requisites

- You have an Oracle DB instance e.g. [Oracle DBMS on Oracle Cloud Infrastructure](https://docs.cloud.oracle.com/iaas/Content/Database/Concepts/databaseoverview.htm?tocpath=Services%7CDatabase%7C_____0)
- You have seeded the `EMPLOYEES` table in your DB schema (check [seed-db.sql](seed-db.sql))

## Setup

### Build the (base) Docker image containing Oracle JDBC driver

- Clone this repo - `git clone https://github.com/abhirockzz/fn-oracledb-java`
- download the Oracle JDBC driver from [this link](https://www.oracle.com/technetwork/database/features/jdbc/default-2280470.html) (`ojdbc7.jar` should be fine) and copy it to the `oracle_driver_docker` folder
- Build a Docker image with the driver JAR (You will use an existing Dockerfile)
	- `cd fn-oracledb-java/oracle_driver_docker`
	- `docker build -t oracle_jdbc_driver_docker .` (if you choose to change the name of the image i.e. `oracle_jdbc_driver_docker`, you'll need to update those references in the `build_image` section of the `func.yaml` for all the functions)

(if successful) You should see an output as below

	Sending build context to Docker daemon  3.401MB
	Step 1/3 : FROM fnproject/fn-java-fdk-build:jdk9-1.0.63
	 ---> 973847bef180
	Step 2/3 : COPY ojdbc7.jar .
	 ---> dd9005799e07
	Step 3/3 : RUN mvn deploy:deploy-file -Durl=file:///function/repo -Dfile=ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1 -Dpackaging=jar
	 ---> Running in 94bce78bf6bb
	[INFO] Scanning for projects...
	Downloading from central: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-clean-plugin/2.5/maven-clean-plugin-2.5.pom
	Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-clean-plugin/2.5/maven-clean-plugin-2.5.pom (3.9 kB at 1.6 kB/s)
	
	............ skipping all the maven magic ..............
	
	Uploading to remote-repository: file:///function/repo/com/oracle/ojdbc7/12.1.0.1/ojdbc7-12.1.0.1.jar
	Uploaded to remote-repository: file:///function/repo/com/oracle/ojdbc7/12.1.0.1/ojdbc7-12.1.0.1.jar (3.4 MB at 6.3 MB/s)
	Uploading to remote-repository: file:///function/repo/com/oracle/ojdbc7/12.1.0.1/ojdbc7-12.1.0.1.pom
	Uploaded to remote-repository: file:///function/repo/com/oracle/ojdbc7/12.1.0.1/ojdbc7-12.1.0.1.pom (392 B at 49 kB/s)
	Downloading from remote-repository: file:///function/repo/com/oracle/ojdbc7/maven-metadata.xml
	Uploading to remote-repository: file:///function/repo/com/oracle/ojdbc7/maven-metadata.xml
	Uploaded to remote-repository: file:///function/repo/com/oracle/ojdbc7/maven-metadata.xml (302 B at 22 kB/s)
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 27.221 s
	[INFO] Finished at: 2018-08-30T02:08:09Z
	[INFO] ------------------------------------------------------------------------
	Removing intermediate container 94bce78bf6bb
	 ---> fc46eb9cc4de
	Successfully built fc46eb9cc4de
	Successfully tagged oracle_jdbc_driver_docker:latest

### Start Fn

- Start local Fn server - `fn start`
- Switch context - `fn use context default`

### Configure Docker

> This is not needed if you don't want to push images to external Docker registry

- `docker login` (use your docker registry credentials)
- `export FN_REGISTRY=<name of your docker repository>`


### Create an app with required database configuration

`fn create app --config DB_URL=<url in format jdbc:oracle:thin:@//host:port/service_name> --config DB_USER=<db username> --config DB_PASSWORD=<password> fn-oradb-java-app` 

e.g. `fn create app --config DB_URL=jdbc:oracle:thin:@//129.220.150.190:1521/test_iad1vc.sub07250801030.faasvcn.oraclevcn.com --config DB_USER=workshop-134 --config DB_PASSWORD=tOps3cr3t fn-oradb-java-app`

## Deploy

Deploy one function at a time. For example, to deploy the `create` function

- `cd fn-oracledb-java/create`
- `fn -v deploy --app fn-oradb-java-app --local --no-bump` (`-v` will activate verbose mode)

> adding `--local` to `fn deploy` will build & push docker images locally (and run it from there). Remove it if you want use a dedicated/external Docker registry

For `read` function deployment

- `cd ../read`
- `fn -v deploy --app fn-oradb-java-app --local --no-bump`

> Repeat for other functions i.e. `delete` and `update`

Run `fn inspect app fn-oradb-java-app` to check your app

> A custom Docker image has been used as `build_image` (see `func.yaml`) - this Docker image pre-packages the Oracle JDBC driver

## Test

.. with Fn CLI using `fn call`

### Create

`echo -n '{"emp_email": "a@b.com","emp_name": "abhishek","emp_dept": "Product Divison"}' | fn call fn-oradb-java-app /create`

If successful, you should a response similar to this `Created employee CreateEmployeeInfo{emp_email=a@b.com, emp_name=abhishek, emp_dept=Product Divison}`

Create as many as you want - make sure that the `emp_email` is unique

### Read

- `fn call fn-oradb-java-app /read` (to fetch all employees)

You should get back a JSON response similar to below

	[
	  {
	    "emp_email": "y@z.com",
	    "emp_name": "abhishek",
	    "emp_dept": "PM"
	  },
	  {
	    "emp_email": "a@b.com",
	    "emp_name": "abhishek",
	    "emp_dept": "Product Divison"
	  },
	  {
	    "emp_email": "x@y.com",
	    "emp_name": "kehsihba",
	    "emp_dept": "QA Divison"
	  }
	]

- `echo -n 'a@b.com' | fn call fn-oradb-java-app /read` (to fetch employee with email `a@b.com`)

		[
		  {
		    "emp_email": "a@b.com",
		    "emp_name": "abhishek",
		    "emp_dept": "Product Divison"
		  }
		]

### Update

It is possible to update the department of an employee

`echo -n '{"emp_email": "a@b.com", "emp_dept": "Support Operations"}' | fn call fn-oradb-java-app /update`

Successful invocation will return back a message similar to `Updated employee UpdateEmployeeInfo{emp_email=a@b.com, emp_dept=Support Operations}`

Check to make sure - `echo -n 'a@b.com' | fn call fn-oradb-java-app /read` - the updated department should reflect

		[
		  {
		    "emp_email": "a@b.com",
		    "emp_name": "abhishek",
		    "emp_dept": "Support Operations"
		  }
		]

### Delete

Use employee email to specify which employee record you want to delete

`echo -n 'a@b.com' | fn call fn-oradb-java-app /delete` and you should see `Deleted employee a@b.com` message

Check to make sure - `echo -n 'a@b.com' | fn call fn-oradb-java-app /read`
