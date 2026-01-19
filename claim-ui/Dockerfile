FROM node:18-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Support both standalone and docker-compose modes
# Use nginx-standalone.conf by default (for standalone)
# In docker-compose, override with nginx.conf via volume mount
ARG NGINX_CONFIG=nginx-standalone.conf
COPY ${NGINX_CONFIG} /etc/nginx/conf.d/default.conf

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]

