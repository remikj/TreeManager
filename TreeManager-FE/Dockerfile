FROM node:18 as build-deps
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . ./
CMD ["npm", "start"]