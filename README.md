# docker-compose-watch
A way to watch and rebuild on change with docker-compose

This is a drop in script to replace `docker-compose`/`docker compose`. Put it
somewhere in your path (`/usr/local/bin` or something) where it will win. It
delegates to `docker compose`.

Known weaknesses:
* It just uses pwd rather than parsing the yaml to decide which contexts to use
* It’s pretty inefficient because it doesn’t know about `.dockerignore` - so a
  change to an ignored file results in the entire context being calculated and
  sent to the docker daemon, before docker then decides there’s nothing to do.
* All services will have their contexts sent to the docker daemon - it should
  be possible I think to limit this by knowing which service's context got
  changed...
* Depends on `fswatch`, you'll need to install that manually

Contributions / criticism welcome.
