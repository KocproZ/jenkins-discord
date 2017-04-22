# Jenkins Discord Webhook Plugin

A lot of developers these days have a Jenkins CI server and many use Discord as a means of communication about their project. With the addition of Discord-compatible GitHub webhooks things almost felt complete.

## The purpose

The Jenkins Discord Webhook plugin was made to share results of a build to a Discord channel using the webhooks that Discord provides. 

Through this plugin you (probably) are able to:
 - [ ] Get success and fail messages about your build.

Yeah, that's it.

## Usage

This plugin uses the post-build feature to execute a request.

Simply install the plugin and select the Discord Webhook in the "Post-Build Actions" dropdown.

![Post-build dropdown with Discord Webhooks selected](https://github.com/jammehcow/jenkins-discord/blob/master/.github/usage_01.jpg)

Then enter your Discord URL in the text box that appears. As simple as that!