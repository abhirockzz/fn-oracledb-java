# Integrating Oracle Functions and Oracle DB

Oracle functions executing `CRUD` operations on Oracle DB. This sample uses a simple `Employee` entity for demonstration purposes

## Pre-requisites

- Create an Oracle DB instance e.g. [Oracle DBMS on Oracle Cloud Infrastructure](https://docs.cloud.oracle.com/iaas/Content/Database/Concepts/databaseoverview.htm?tocpath=Services%7CDatabase%7C_____0)
- Create the `EMPLOYEES` table in your DB schema using [seed-db.sql](seed-db.sql)

### Configure Oracle Maven repository

This application uses Oracle JDBC driver which is extracted from the Oracle Maven repository. To access the Oracle Maven Repository, you must first register for access on Oracle Technology Network (if you haven't already). You can access the registration application at https://www.oracle.com/webapps/maven/register/license.html

This application displays the license agreement for access to the Oracle Maven Repository. You must accept the license agreement to access the Oracle Maven Repository. 

For further details, please refer to https://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm#MAVEN9010

## Create an app with required database configuration

`fn create app fn-oradb-java-app --annotation oracle.com/oci/subnetIds='["<SUBNET_OCID>"]' --config DB_URL=jdbc:oracle:thin:@//<HOST_IP>:<PORT>/<SERVICE_NAME> --config DB_USER=<DB_USER> --config DB_PASSWORD=<DB_PASSWORD>` 

e.g. `fn create app fn-oradb-java-app --annotation oracle.com/oci/subnetIds='["ocid1.subnet.oc1.phx.aaaaaaaaghmsma7mpqhqdhbgnby25u2zo9wqlrrcskvu7jg56dryxt3hgvkz"]' --config DB_URL=jdbc:oracle:thin:@//129.777.888.999:1521/test_iad1vc.sub09250801039.myvcn.oraclevcn.com --config DB_USER=dba_user --config DB_PASSWORD=s3cr3t`

## Build process

A custom Dockerfile is used to build the function(s). The build process accepts the OTN username and password along with a master password as inputs. The master password itself is encrypted using `mvn --encrypt-master-password` and it is then used  to encrypt your OTN credentials. The encrypted master password is stored in `settings-security.xml` and the encrypted OTN credentials are stored in `settings.xml`. These XMLs are then referred by `mvn package`.

## Deploy

> Use your OTN username and password (created in the **Configure Oracle Maven repository** step) for `ORACLE_USERID` and `ORACLE_PASSWORD` respectively. You can use any value for the `MASTER_PASSWORD`

### Deploy all the functions together

- `cd fn-oracledb-java`
- `fn -v deploy --app fn-oradb-java-app --build-arg MASTER_PASSWORD=<maven_master_password> --build-arg ORACLE_USERID=<OTN_USERNAME> --build-arg ORACLE_PASSWORD=<OTN_PASSWORD> --all` 

e.g. `fn -v deploy --app fn-oradb-java-app --build-arg MASTER_PASSWORD=foobar --build-arg ORACLE_USERID=abhishek.af.gupta@oracle.com --build-arg ORACLE_PASSWORD=t0ps3cr3t --all`

> Notice the usage of `--all` flag at the end of the `deploy` command. It uses the app name as specified in `app.yaml`

### Deploy one function at a time

For example if you just want to deploy the `create` function

- `cd fn-oracledb-java/create`
- `fn -v deploy --app fn-oradb-java-app --build-arg MASTER_PASSWORD=<maven_master_password> --build-arg ORACLE_USERID=<OTN_USERNAME> --build-arg ORACLE_PASSWORD=<OTN_PASSWORD>`

## Test

### Create Employee

`echo -n '{"emp_email": "a@b.com","emp_name": "abhishek","emp_dept": "Product Divison"}' | fn invoke fn-oradb-java-app create-emp`

If successful, you should a response similar to this `Created employee CreateEmployeeInfo{emp_email=a@b.com, emp_name=abhishek, emp_dept=Product Divison}`

Create as many as you want - make sure that the `emp_email` is unique

### Read Employee(s)

- `fn invoke fn-oradb-java-app read-emp` (to fetch all employees)

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

- `echo -n 'a@b.com' | fn invoke fn-oradb-java-app read-emp` (to fetch employee with email `a@b.com`)

		[
		  {
		    "emp_email": "a@b.com",
		    "emp_name": "abhishek",
		    "emp_dept": "Product Divison"
		  }
		]

### Update Employee

It is possible to update the department of an employee

`echo -n '{"emp_email": "a@b.com", "emp_dept": "Support Operations"}' | fn invoke fn-oradb-java-app update-emp`

Successful invocation will return back a message similar to `Updated employee UpdateEmployeeInfo{emp_email=a@b.com, emp_dept=Support Operations}`

Check to make sure - `echo -n 'a@b.com' | fn invoke fn-oradb-java-app read-emp` - the updated department should reflect

		[
		  {
		    "emp_email": "a@b.com",
		    "emp_name": "abhishek",
		    "emp_dept": "Support Operations"
		  }
		]

### Delete Employee

Use employee email to specify which employee record you want to delete

`echo -n 'a@b.com' | fn invoke fn-oradb-java-app delete-emp` and you should see `Deleted employee a@b.com` message

Check to make sure - `echo -n 'a@b.com' | fn invoke fn-oradb-java-app read-emp`
