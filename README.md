radicle-github-migrate
=====================

![Build](https://github.com/cytechmobile/radicle-github-migrate/workflows/build/badge.svg)

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#features">Features</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#command-line-interface">Command-line interface</a></li>
        <li><a href="#requirements">Requirements</a></li>
        <li><a href="#important-notes">Important Notes</a></li>
        <li><a href="#environment-variables">Environment Variables</a></li>
      </ul>
    </li>
    <li>
      <a href="#binaries">Binaries</a>
      <ul>
        <li><a href="#jar-binary">JAR Binary</a></li>
        <li><a href="#native-binaries">Native Binaries</a></li>
        <li><a href="#docker-image">Docker Image</a></li>
        <li><a href="#pre-built-binaries">Pre-built binaries</a></li>
      </ul>
    </li>
    <li><a href="#building-from-source">Building from source</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

## About The Project
This Command Line Interface (CLI) tool enables you to seamlessly migrate issues from your GitHub repository to your Radicle project.

To utilize this tool, you have a few options:
* You can download one of the pre-built binaries from the project's GitHub [releases](https://github.com/cytechmobile/radicle-github-migrate/releases). 
* Alternatively, you can use the provided docker image: `docker pull ghcr.io/cytechmobile/radicle-github-migrate:latest` 
* If you prefer, you can also build a binary directly from the source code.

The target rad environment must have a version `0.8.0` rad Command Line Interface (CLI) tool installed and the HTTP daemon (radicle-httpd) up and running. Installation instructions for `rad` are available [here](https://github.com/radicle-dev/heartwood).

This tool is available under [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

**Important Note:** Please note that since Radicle is still under active development, our functionality may require adjustments in the future to ensure compatibility.

### Features
The tool offers several important features, including:
* It enables the migration of all GitHub issues from the source repository in a single run.
* It migrates essential information such as the `Title`, `Description`, `Status`, `Labels`, `Comments`, `Events`, and `Milestone` details.
* It supports the migration of inline assets/files discovered within GitHub issues and comments as Radicle embeds. A GitHub `user_session` cookie must be provided when migrating assets/files from a private GitHub repository by using either the `--gh-session` CLI parameter or the `GH_SESSION` environment variable. Login to GitHub via your browser and copy the value of the `user_session` cookie. 
* Any additional information that doesn't fit within the Issue model is preserved in a dedicated `GitHub Metadata` section, along with references to the original repository
* It supports incremental migration, allowing you to rerun the tool (e.g., on a schedule) and create only the newest issues that haven't been previously migrated.
* It offers a range of filtering options to streamline the issue migration process, including issues created after a specified time, issues with specific labels, issues in a particular state, issues belonging to a given milestone number, issues created by a specific user, and issues assigned to a particular user.
* It is available in different binary forms, providing flexibility in how it can be utilized.

## Usage

### Command-line interface
```bash 
Usage: radicle-github-migrate issues [-gv=<gVersion>] [-gu=<gUrl>] -gr=<gRepo> -go=<gOwner> -gt [-gs=<gSession>] [-rv=<rVersion>] [-ru=<rUrl>] -rp=<rProject> -rh [-fs=<fSince>] [-fl=<fLabels>] [-ft=<fState>] [-fm=<fMilestone>] [-fa=<fAssignee>] [-fc=<fCreator>] [-dr]

Migrate issues from a GitHub repository to a Radicle project.       
   
      -gv, --gh-api-version=<gVersion>      The version of the GitHub REST API (default: 2022-11-28).
      -gu, --gh-api-url=<gUrl>              The base url of the GitHub REST API (default: https://api.github.com).
      -gr, --gh-repo=<gRepo>                The source GitHub repo.
      -go, --gh-repo-owner=<gOwner>         The owner of the source GitHub repo.
      -gt, --gh-token                       Your GitHub personal access token (with repo scope or read-only access granted).
      -gs, --gh-session                     The value of the user_session cookie. It is utilized for migrating assets and files from a private GitHub repository.
      -rv, --rad-api-version=<rVersion>     The version of the Radicle HTTP API (default: v1).
      -ru, --rad-api-url=<rUrl>             The base url of Radicle HTTP API (default: http://localhost:8080/api).
      -rp, --rad-project=<rProject>         The target Radicle project.
      -rh, --rad-passphrase=<rPassphrase>   Your radicle passphrase.
      -fs, --filter-since=<fSince>          Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
      -fl, --filter-labels=<fLabels>        Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
      -ft, --filter-state=<fState>          Migrate issues in this state (default: all, can be one of: open, closed, all).
      -fm, --filter-milestone=<fMilestone>  Migrate issues belonging to the given milestone number (example: 3).
      -fa, --filter-assignee=<fAssignee>    Migrate issues assigned to the given user name.
      -fc, --filter-creator=<fCreator>      Migrate issues created by the given user name.
      -dr, --dry-run                        Run the whole migration process without actually creating the issues in the target Radicle project.
```
### Requirements
To use this application, you'll need to fulfill some common requirements, as well as specific requirements based on the binary you choose:
* A GitHub account with a Personal Access Token (PAT). Your PAT must grant you with `repo` scope - in case of a classic one - or with `read-only access` - in case of a fine-grained one. 
* The `rad` Command Line Interface (CLI) tool installed, preferably version 0.8.0 or later. You can find installation details [here](https://github.com/radicle-dev/heartwood). To check the version, run `rad --version`.
* A running instance of the Radicle HTTP daemon. Before starting the daemon, ensure that you have set the `RAD_PASSPHRASE` environment variable or executed the `rad auth` command in the same terminal. Refer to [this link](https://github.com/radicle-dev/heartwood/blob/master/radicle-cli/examples/rad-auth.md) for examples on how to use the `rad auth` command. To start the Radicle HTTP daemon, run `radicle-httpd`.
* A Radicle-initialized Git repository. This will serve as your target Radicle project, where the issues will be migrated.
* If running the JAR binary, ensure that you have Java 17 or a later version installed on your machine.
* If using the Docker image, make sure that the Docker daemon is up and running on your machine.

### Important Notes 
Since the Issue model and HTTP API in Radicle are currently simpler compared to GitHub, we have implemented the following alternative solutions / adaptations:
* We have included a special `GitHub Metadata` section at the beginning of the `Description` and each `Comment` of the Radicle Issue. This section contains extra information and links to the original GitHub Issue, presented in a table format with columns such as `Issue Number`, `Created On`, `Created By`, `Assignees`, `Milestone`, and `Due By`.
* GitHub `Events` are migrated as Radicle `Comments`.
* GitHub `Milestones` are migrated as Radicle `Labels`. Additional information can be found in the `GitHub Metadata` section in the `Description` of each Radicle `Issue` (if there is any).
* The user who runs the tool will be listed as the `Creator` of all Radicle issues.
* Any links to images within the `Description` and `Comments` of GitHub Issues will still point to GitHub's servers. This means that images will display correctly for public GitHub repositories. However, for private repositories, since authorization is required, you will need to copy the source URL and access it through your browser.

### Environment Variables
You can pass any of the command line options via environment variables. Here is the complete list of the supported environment variables:
* GH_API_VERSION: The version of the GitHub REST API (default 2022-11-28)
* GH_API_URL: The base url of the GitHub REST API (default https://api.github.com)
* GH_REPO: The source GitHub repo
* GH_OWNER: The owner of the source GitHub repo
* GH_TOKEN: Your GitHub personal access token (with `repo` scope or `read-only access` granted).
* GH_SESSION: The value of the user_session cookie. It is utilized for migrating assets and files from a private GitHub repository.
* RAD_API_VERSION: The version of the Radicle HTTP API (default v1)
* RAD_API_URL: The base url of Radicle HTTP API (default http://localhost:8080/api)
* RAD_PROJECT: The target Radicle project
* RAD_PASSPHRASE: Your radicle passphrase
* FILTER_SINCE: Migrate issues created after the given time (default: lastRun in store.properties file, example: 2023-01-01T10:15:30+01:00).
* FILTER_LABELS: Migrate issues with the given labels given in a csv format (example: bug,ui,@high).
* FILTER_STATE: Migrate issues in this state (default: all, can be one of: open, closed, all).
* FILTER_MILESTONE: Migrate issues belonging to the given milestone number (example: 3).
* FILTER_ASSIGNEE: Migrate issues assigned to the given username.
* FILTER_CREATOR: Migrate issues created by the given username.
* LOG_LEVEL: The log level of the application (default INFO)
* STORAGE_FILE_PATH: The path of the storage properties files (default store.properties)
* DRY_RUN: Run the whole migration process without actually creating the issues in the target Radicle project.

For example, to run the command in DEBUG mode, you can execute the following command:

```shell
LOG_LEVEL=DEBUG java -jar radicle-github-migrate-0.1.0.jar issues
```

## Binaries

### JAR Binary
If you intend to use a specific version of the JAR binary (e.g., 0.1.0), execute the following command:
```bash
java -jar radicle-github-migrate-0.1.0.jar issues
```

### Native Binaries
If you intend to use one of the native builds, you need to execute the corresponding native binary. For example, if you have downloaded the binary for Ubuntu, you should execute it by running the following command:
```bash 
./radicle-github-migrate-0.1.0-ubuntu-latest issues
```
### Docker Image
If you intend to use the Docker image, please follow the instructions provided below:

```shell
# Pull the docker image in your local docker registry
docker pull ghcr.io/cytechmobile/radicle-github-migrate:latest

# Tag the docker image in your local docker registry
docker tag ghcr.io/cytechmobile/radicle-github-migrate:latest radicle-github-migrate

# Run the migration
docker run -it -v .:/root/config -v ~/.radicle:/root/.radicle -e RAD_PASSPHRASE=<YOUR_PASSPHRASE> radicle-github-migrate issues
```
To ensure that the `docker run` command executes successfully, the following volumes are required:
* `.:/root/config`: This allows the tool to write a `store.properties` file in your current directory, which helps maintain its state across subsequent runs. IMPORTANT: Please ensure that the folder from which you run the tool has the appropriate write permissions.
* `~/.radicle:/root/.radicle`: This enables the tool to access your Radicle path. If the `rad path` command returns a different path, please update the volume accordingly.

The image assumes that your `radicle-httpd` service runs by default at `http://172.17.0.1:8080/api`, where `172.17.0.1` represents the IP address of the host from inside the Docker container. If you need to change this default configuration, you can utilize the available environment variables or CLI options provided.

Lastly, you have the option to pass any environment variable using the -e option of the docker run command. For example: `docker run -e LOG_LEVEL=DEBUG`.

### Pre-built binaries
Pre-built binaries can be downloaded from the project's GitHub [releases page](https://github.com/cytechmobile/radicle-github-migrate/releases). Choose the appropriate release for your operating system and download the associated JAR or executable file.


## Building from source
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
$ ./mvnw package -Pdocker
```

## License

Distributed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See `LICENSE` for more information.
