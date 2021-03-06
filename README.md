# docker-compose-watch
A way to watch and rebuild on change with docker-compose

This is a drop in script to replace `docker-compose`/`docker compose`. Copy
[docker-compose](docker-compose) somewhere in your path (`/usr/local/bin` or
something) where it will win. It delegates to `docker compose`.

Run `docker-compose up --build --watch` and then when you edit files in that
directory the relevant images will be built & containers recreated.

Layer caching should mean that the builds are fast if nothing relevant has
changed, and if the resulting image is unchanged then the container will not be
restarted.

## Dependencies:
* Depends on `fswatch`, you'll need to install that manually
* Uses compose v2, you'll need to
  [install docker compose v2 manually](https://docs.docker.com/compose/cli-command/#installing-compose-v2)
  if `docker compose version` doesn't currently work for you.

## Known weaknesses:
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

## TO DO
1. Handle `-f` links to dockerfiles - including multiple of them. Also handle
   `docker-compose.override.yml`.
2. Use a cli yaml parser to read the `services.<service_name>.build` or 
   `services.<service_name>.build.context` from each service in each of these
   (depending on whether it's a string or an object) The context will likely 
   be a relative path - if it is, it must be 
   [resolved relative to the first -f compose file](https://docs.docker.com/compose/extends/#understanding-multiple-compose-files)
4. Listen to all of those context directories - should be possible in a single
   `fswatch` subprocess.
5. On change, work out which service(s) context(s) the change was in, and pass
   their names to `docker compose up`:
   `docker compose up -d --build <service1> <service2> ...`

I think that will produce something usable so long as you use
`DOCKER_BUILDKIT=1`, have an appropriate `.dockerignore` & factor your
Dockerfile for sensible layer caching, without going to the lengths of trying to
calculate whether the change is excluded by a `.dockerignore` in bash.

Contributions / criticism welcome.
