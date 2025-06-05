FROM node:18-alpine AS build

WORKDIR /app

# Аргументы сборки для определения доменного имени API
ARG API_HOST=localhost

# Копируем файлы зависимостей
COPY package.json package-lock.json ./

# Устанавливаем зависимости
RUN npm ci

# Копируем исходный код
COPY . .

# Переопределяем .env.production с актуальным IP/доменом
RUN echo "VITE_API_BASE_URL=http://${API_HOST}/api" > .env.production && \
    echo "VITE_WS_BASE_URL=ws://${API_HOST}/ws" >> .env.production

# Создаем production сборку
RUN npm run build

# Финальный образ с Nginx для раздачи статических файлов
FROM nginx:1.23-alpine

# Копируем статические файлы из этапа сборки
COPY --from=build /app/dist /usr/share/nginx/html

# Копируем кастомную конфигурацию Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]