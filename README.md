radicle-github-migrate
=====================

![Build](https://github.com/cytechmobile/radicle-github-migrate/workflows/build/badge.svg)

This Command Line Interface (CLI) tool allows you to migrate the issues from your GitHub repository to your Radicle project.

To use this tool, you can either download one of the pre-built binaries from the project's GitHub [releases](https://github.com/cytechmobile/radicle-github-migrate/releases), the docker image or you can build a binary from the source code.

The target rad environment must have a version 0.8.0 rad Command Line Interface (CLI) tool installed and the HTTP daemon (radicle-httpd) up and running. Installation instructions for `rad` are available [here](https://github.com/radicle-dev/heartwood).

This tool is available under [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Command-line interface
```bash 
Usage: radicle-github-migrate issues [-gv=<gVersion>] [-gu=<gUrl>] -gr=<gRepo> -go=<gOwner> -gt [-rv=<rVersion>] [-ru=<rUrl>] -rp=<rProject> [-fs=<fSince>] [-fl=<fLabels>] [-ft=<fState>] [-fm=<fMilestone>] [-fa=<fAssignee>] [-fc=<fCreator>] 

Migrate issues from a GitHub repository to a Radicle project.       
   
      -gv, --github-api-version=<gVersion>     The version of the GitHub REST API (default: 2022-11-28).
      -gu, --github-api-url=<gUrl>          The base url of the GitHub REST API (default: https://api.github.com).
      -gr, --github-repo=<gRepo>            The source GitHub repo.
      -go, --github-repo-owner=<gOwner>     The owner of the source GitHub repo.
      -gt, --github-token                   Your GitHub personal access token.
      -rv, --radicle-api-version=<rVersion> The version of the Radicle HTTP API (default: v1).
      -ru, --radicle-api-url=<rUrl>         The base url of Radicle HTTP API (default: http://localhost:8080/api).
      -rp, --radicle-project=<rProject>     The target Radicle project.
      -fs, --filter-since=<fSince>          Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
      -fl, --filter-labels=<fLabels>        Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
      -ft, --filter-state=<fState>          Migrate issues in this state (default: all, can be one of: open, closed, all).
      -fm, --filter-milestone=<fMilestone>  Migrate issues belonging to the given milestone number (example: 3).
      -fa, --filter-assignee=<fAssignee>    Migrate issues assigned to the given user name.
      -fc, --filter-creator=<fCreator>      Migrate issues created by the given user name.
```
### Requirements
To use this application there are some common and some binary specific requirements:
* A GitHub account with a personal access token (common)
* The `rad` Command Line Interface (CLI) tool installed, at least v0.8.0. Please check [here](https://github.com/radicle-dev/heartwood) for installation details.  To check the version run `rad --version` (common)
* A running instance of the Radicle HTTP deamon. Before you start the deamon, make sure that you have set the `RAD_PASSPHRASE` environment variable or have executed the `rad auth` command on the same terminal. Refer to [this link](https://github.com/radicle-dev/heartwood/blob/master/radicle-cli/examples/rad-auth.md) for examples on how to use the `rad auth` command. To start the Radicle HTTP daemon run `radicle-httpd`. (common)
* A radicle initialised Git repo. This will be your target radicle project where the issues will be migrated into. (common)
* To run the JAR binary you will need Java 17 or later installed on your machine (JAR Binary)
* To use the docker image you need the docker deamon up and running on your machine (Docker Image)

### JAR Binary
If you plan to use a specific version of the JAR binary (e.g. 0.1.0) run the following command:
```bash
java -jar radicle-github-migrate-0.1.0.jar issues
```

### Native Binaries
If you plan to use one of the native builds, you must execute the corresponding native binary. For instance, if you downloaded the binary for Ubuntu, you should execute it by running the following command:
```bash 
./radicle-github-migrate-0.1.0-ubuntu-latest issues
```
### Docker Image
If you plan to use the docker image, follow the instructions below:

```shell
# Pull the docker image in your local docker registry
docker pull ghcr.io/cytechmobile/radicle-github-migrate:latest

# Tag the docker image in your local docker registry
docker tag ghcr.io/cytechmobile/radicle-github-migrate:latest radicle-github-migrate

# Run the migration
docker run -it -v .:/root/config -v ~/.radicle:/root/.radicle -v $SSH_AUTH_SOCK:/ssh-agent radicle-github-migrate issues
```
As you can see, in order for the `docker run` command to run properly the following volumes are required:
* `.:/root/config`: It allows the tool to write a store.properties file in your current directory in order to keep its state among subsequent runs.
* `~/.radicle:/root/.radicle`: It allows the tool to access your radicle path. In case the `rad path` command returns another path update the volume respectively.
* `$SSH_AUTH_SOCK:/ssh-agent`: It allows the application to access your ssh agent for authorizing the sessions

The image assumes that your radicle-httpd service runs by default at `http://172.17.0.1:8080/api`, where `172.17.0.1` is the IP of the host from inside the docker container. You can change it by using the available environment variables or cli options.

Finally, you can pass any environment variable by using the -e option of the docker run command: `docker run -e LOG_LEVEL=DEBUG`

### Environment Variables
You can pass any of the command line options via environment variables. Here is the complete list of the supported environment variables:
* GITHUB_API_VERSION: The version of the GitHub REST API (default 2022-11-28)
* GITHUB_API_URL: The base url of the GitHub REST API (default https://api.github.com)
* GITHUB_REPO: The source GitHub repo
* GITHUB_OWNER: The owner of the source GitHub repo
* GITHUB_TOKEN: Your GitHub personal access token
* RADICLE_API_VERSION: The version of the Radicle HTTP API (default v1)
* RADICLE_API_URL: The base url of Radicle HTTP API (default http://localhost:8080/api)
* RADICLE_PROJECT: The target Radicle project
* FILTER_SINCE: Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
* FILTER_LABELS: Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
* FILTER_STATE: Migrate issues in this state (default: all, can be one of: open, closed, all).
* FILTER_MILESTONE: Migrate issues belonging to the given milestone number (example: 3).
* FILTER_ASSIGNEE: Migrate issues assigned to the given username.
* FILTER_CREATOR: Migrate issues created by the given username.
* LOG_LEVEL: The log level of the application (default INFO)
* STORAGE_FILE_PATH: The path of the storage properties files (default store.properties)

For example, to run the command in DEBUG mode, you can execute the following command:

```shell
LOG_LEVEL=DEBUG java -jar radicle-github-migrate-0.1.0.jar issues
```

### Building from source
To build the binary from source code, follow these steps:
1.  Clone the repository from GitHub:
```shell
$ git clone https://github.com/cytechmobile/radicle-github-migrate.git
```

2. Change into the project directory:

```shell
$ cd radicle-github-migrate
```

3. Build the binaries using Maven:

To build JAR binary in Unix run:
```shell
$ ./mvnw package
```

To build a native binary in Unix run:
```shell
$ ./mvnw package -Pnative
```

This will generate the binary file in the `target` directory.

To build a docker image in Unix run:
```shell
$ ./mvnw package -pdocker
```

### Downloading pre-built binaries
Pre-built binaries can be downloaded from the project's GitHub [releases page](https://github.com/cytechmobile/radicle-github-migrate/releases). Choose the appropriate release for your operating system and download the associated JAR or executable file.
