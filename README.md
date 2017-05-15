# Discord Notifier

Discord Notifier provides a bridge between Jenkins and Discord through the built-in webhook functionality.

This project is in beta.

## The purpose

The Jenkins Discord Webhook plugin was made to share results of a build to a Discord channel using the webhooks that Discord provides. 

Through this plugin you are able to:
 - [x] Get success and fail messages about your build
 - [x] Link to build artifacts
 - [x] List SCM changes to the build

Yeah, that's it aye.

## Download

You'll have to manually install the plugin via the advanced tab of your plugin settings.
A Jenkins plugin repo build will be available soon.

## Usage

This plugin uses the post-build feature to execute a request.

Simply install the plugin and select the Discord Webhook in the "Post-Build Actions" dropdown.

![Post-build dropdown with Discord Webhooks selected](https://github.com/jammehcow/jenkins-discord/blob/master/.github/usage_01.jpg)

Then enter your Discord URL in the text box that appears.
As simple as that!