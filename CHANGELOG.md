# radicle-migration-tool Changelog

## 0.5.0 - 2024-04-05
### Features
* Added support for migrating GitHub Wikis. The tool provides a convenient way to migrate your GitHub Wiki to your Radicle project under the `.wiki` directory, offering a quick solution until Radicle potentially introduces support for Wikis.
* Added support for migrating GitLab Issues. The tool includes comprehensive support for migrating GitLab Issues, providing all the features and flexibility already available for migrating GitHub issues.

## 0.4.0 - 2023-10-19
### Features
* Added support for migrating the inline assets/files discovered within GitHub issues and comments as Radicle embeds. 
* Removed dependency on the ssh agent by using the private key to sign the session. 

## 0.3.1 - 2023-08-23
### Features
* Support for multi-arch Docker images has been added. The current pre-built Docker image now includes support for both linux/amd64 and linux/arm64 architectures.

## 0.3.0 - 2023-08-09
### Fixes
* Renamed the `tags` field to `labels` in order to comply with the updated model of the `Radicle Issue`
* Updated the `comment` action in order to comply with the updated endpoint of the Radicle HTTP API

## 0.2.0 - 2023-07-20
### Features
* Added support for incremental migrations, allowing you to rerun the tool (e.g., on a schedule) and create only the newest issues that haven't been previously migrated.
* Added support for migrating important GitHub Events
* Added support for migrating GitHub Milestones
* Added support for a range of filtering options to streamline the issue migration process, including issues created after a specified time, issues with specific labels, issues in a particular state, issues belonging to a given milestone number, issues created by a specific user, and issues assigned to a particular user.
* Added a special `GitHub Metadata` section. Any additional information that doesn't fit within the Issue model is preserved in that section, along with references to the original repository
* Added support for running the migration tool in the form of a docker container
* Added a `--dry-run` option allows users to run the whole migration process without actually creating the issues in the target Radicle project

## 0.1.0 - 2023-05-11
### Features
* Migrate GitHub issues to a Radicle project (supported attributes `title`, `description`, `tags`, `state`, `comments`).
* Annotate migrated issues and comments with GitHub metadata (`owner`, `assignees`, `creation date`)
