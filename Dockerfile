# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制JAR文件到容器中
COPY EduFlow/springboot/target/*.jar app.jar

# 暴露应用端口
EXPOSE 9090

# 设置环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=9090"]
