FROM nginx
COPY nginx.conf /etc/nginx/nginx.conf
COPY self-signed.conf /etc/nginx/snippets/self-signed.conf
COPY public /usr/share/nginx/html
COPY keys/nginx.crt /etc/ssl/certs/nginx.crt
COPY keys/nginx.key /etc/ssl/private/nginx.key
COPY keys/dhparam.pem /etc/nginx/dhparam.pem
RUN mkdir -p  /var/log/nginx
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]