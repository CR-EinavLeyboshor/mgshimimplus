FROM nginx:latest
RUN rm /etc/nginx/conf.d/default.conf
COPY default.conf /etc/nginx/conf.d/
COPY ./site/* /usr/share/nginx/html/