# Fn with Oracle DB

## Set up

- `git clone https://github.com/abhirockzz/fn-oracledb-java`
- replace `config` section `func.yaml` with DB connectivity details for your specific environment

## Start...

- `fn start`

Configure Docker

- `docker login` (use your docker registry credentials)
- `export FN_REGISTRY=<name of your docker repository>`

> your function docker image name will end up being - `<docker repo name>/<function name in func.yaml>:<version in func.yaml>`

Moving on....

- `cd fn-oracledb-java`
- `fn -v deploy --all` (`-v` will activate verbose mode)

> adding `--local` to `fn deploy` will build & push docker images locally (and run it from there). Remove it if you want use a dedicated/external Docker registry

All your functions (create, read, update, delete) should now be deployed. Check it using `fn inspect app fn-oradb-java-app` and `fn list routes fn-oradb-java-app`

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
