# EasyHarvest
EasyHarvest aims to simplify the deployment and controlled execution of large-scale sensing applications on smartphones. On the one hand, application owners submit to a server sensing tasks for distribution on smartphones, and collect the data produced by them in a simple manner. On the other hand, smartphone owners control the execution of sensing tasks on their devices through a single interface, without having to repeatedly download, install and configure individual sensing applications. The interaction between the smartphone and the server occurs in a transparent way, with tolerance to intermittent connectivity and support for disconnected operation.

*For more information read* [here](http://www.inf.uth.gr/wp-content/uploads/formidable/Katsomallos_Emmanouil1.pdf) *(slightly outdated) and* [here](http://www.inf.uth.gr/wp-content/uploads/formidable/Katsomallos_Emmanouil1.pdf).

## Initial Setup

### Database
```sql
CREATE DATABASE server;
```

```sql
USE server;
```

```sql
CREATE TABLE users(
  username TEXT,
  password TEXT,
  email    TEXT
);
```

```sql
CREATE TABLE tasks(
  id         NUMERIC,
  filename   TEXT,
  username   TEXT,
  ready      TEXT,
  downloaded NUMERIC
);
```

```sql
CREATE TABLE devices(
  id        INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username  TEXT,
  model     TEXT,
  os        TEXT,
  reg_date  TEXT,
  reg_time  TEXT,
  last_date TEXT,
  last_time TEXT
);
```

```sql
CREATE TABLE pms(
  id       NUMERIC,
  filename TEXT,
  username TEXT,
  sensing  NUMERIC,
  ready    TEXT
);
```

### Paths and Variables
com.www&#8203;.server > Globals.java

| Parameter     | Value         |
| ------------- | ------------- |
| db_dir        | This is where user data and files are saved. Simply create a folder and enter its path. |
| db_server     | The address of the MySQL database (“jdbc:mysql://localhost:port/server”). |
| db_username   | The username of the MySQL database we previously created. |
| db_password   | The password of the MySQL database user. |
| server_irl    | The localhost url with the apache port and the application name (Server). |
| lib_url       | The location of the android.jar library file (e.g. “…/Android/sdk/platforms/android-X/android.jar”). |
| console_cmd   | The console application (e.g. for Windows it is “cmd”). |
| javac_cmd     | The location of the javac binary. |
| dx_cmd        | The location of the android dx tool (e.g. “…/Android/sdk/build-tools/X/lib/dx”). |
| zip_cmd       | Preferred compressing application. |
| zip_args      | Zip command arguments (if any). |

### Start the Server
When the Server starts, to check if everything is OK, run from your browser the URL:

`http://localhost:port/Server/webresources/tasks`

The above URL should show the message “TaskService is ready.”

To start using the EasyHarvest Server, simply create an account by signing up.
