# docker-compose-watch
A way to watch and rebuild on change with docker-compose

This is a drop in script to replace `docker-compose`/`docker compose`. Put it
somewhere in your path (`/usr/local/bin` or something) where it will win. It
delegates to `docker compose`.

Run `docker-compose up --build --watch` and then when you edit files in that
directory the relevant images will be built & containers recreated.

Layer caching should mean that the builds are fast if nothing relevant has
changed, and if the resulting image is unchanged then the container will not be
restarted.

Known weaknesses:
* It just uses pwd rather than parsing the yaml to decide which contexts to use
* It’s pretty inefficient because it doesn’t know about `.dockerignore` - so a
  change to an ignored file results in the entire context being calculated and
  sent to the docker daemon, before docker then decides there’s nothing to do.

  *NOTE* - actually this is better than I had thought:
  1. Obviously `.dockerignore` is avaluated before sending to the context, so
     while changes in files matched by `.dockerignore` will trigger a rebuild
     they won't go to the context
  2. so long as you are using `DOCKER_BUILDKIT=1` buildkit also evaluates the
     `COPY` statements in the `Dockerfile` before sending the context, so only
     relevant files are sent to the context
* All services will have their contexts sent to the docker daemon - it should
  be possible I think to limit this by knowing which service's context got
  changed...
* Depends on `fswatch`, you'll need to install that manually

Contributions / criticism welcome.
