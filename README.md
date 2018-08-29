# Fn with Oracle DB

Fn functions executing `CRUD` operations on Oracle DB. This sample uses a simple `Employee` entity for demonstration purposes 

## Pre-requisites

- You have an Oracle DB instance e.g. [Oracle DBMS on Oracle Cloud Infrastructure](https://docs.cloud.oracle.com/iaas/Content/Database/Concepts/databaseoverview.htm?tocpath=Services%7CDatabase%7C_____0)
- You have seeded the `EMPLOYEES` table in your DB schema (check [seed-db.sql](seed-db.sql))

## Start Fn

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

Clone this repo - `git clone https://github.com/abhirockzz/fn-oracledb-java`

Deploy one function at a time. For example, to deploy the `create` function

- `cd fn-oracledb-java/create`
- `fn -v deploy --app fn-oradb-java-app --local --no-bump` (`-v` will activate verbose mode)

> adding `--local` to `fn deploy` will build & push docker images locally (and run it from there). Remove it if you want use a dedicated/external Docker registry

For `read` function deployment

- `cd fn-oracledb-java/read`
- `fn -v deploy --app fn-oradb-java-app --local --no-bump`

> Repeat for other functions i.e. `delete` and `update`

Run `fn inspect app fn-oradb-java-app` to check your app

## Test

.. with Fn CLI using `fn call`

### Create

`echo -n '{"emp_email": "a@b.com","emp_name": "abhishek","emp_dept": "Product Divison"}' | fn call fn-oradb-java-app /create`

Create as many as you want

### Read

- `fn call fn-oradb-java-app /read` (to fetch all employees)
- `echo a@b.com | fn call fn-oradb-java-app /read` (to fetch employee with email `a@b.com`)

### Update

It is possible to update the department of an employee

`echo -n '{"emp_email": "a@b.com", "emp_dept": "Support Operations"}' | fn call fn-oradb-java-app /update`

> check to make sure - `echo a@b.com | fn call fn-oradb-java-app /read`

### Delete

Use employee email to specify which employee record you want to delete

`echo -n a@b.com | fn call fn-oradb-java-app /delete`

> check to make sure - `echo a@b.com | fn call fn-oradb-java-app /read`
