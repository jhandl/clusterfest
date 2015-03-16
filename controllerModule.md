[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)

# Controller Module #

The controller module allows you to start, pause, resume, stop and kill nodes.

To start and kill nodes, the module sends a command via SSH to execute start.sh and stop.sh in the node install dir. This obviously works on unix-like systems only. If the private/public key pair is not properly setup to allow password-less access from the server to the node machine, this will fail.

Pause, resume and stop are sent through the controllable interface via RPC.