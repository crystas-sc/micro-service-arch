
# svelte app

This is a project template for [Svelte](https://svelte.dev) apps. It lives at https://github.com/sveltejs/template.

To create a new project based on this template using [degit](https://github.com/Rich-Harris/degit):

```bash
npx degit sveltejs/template svelte-app
cd svelte-app
```

*Note that you will need to have [Node.js](https://nodejs.org) installed.*


## Get started

Install the dependencies...

```bash
cd svelte-app
npm install
```

...then start [Rollup](https://rollupjs.org):

```bash
npm run dev
```


# keys
```
cd keys
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx.key -out nginx.crt
openssl dhparam -out dhparam.pem 256

docker rm -f $(docker ps -aq -f name=nginx-container)
docker run --name nginx-container -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf:ro  -v $(pwd)/self-signed.conf:/etc/nginx/snippets/self-signed.conf:ro -v $(pwd)/public:/usr/share/nginx/html:ro -v $(pwd)/keys/nginx.crt:/etc/ssl/certs/nginx.crt:ro -v $(pwd)/keys/nginx.key:/etc/ssl/private/nginx.key:ro  -v $(pwd)/keys/dhparam.pem:/etc/nginx/dhparam.pem:ro -p 80:80 -p443:443  -it nginx
```