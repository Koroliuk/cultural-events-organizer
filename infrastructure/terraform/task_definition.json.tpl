[
   {
      "essential": true,
      "name":"event-management-service",
      "image":"${REPOSITORY_URL}",
      "portMappings":[
         {
            "containerPort":8080,
            "hostPort":8080,
            "protocol":"tcp"
         }
      ],
      "logConfiguration": {
          "logDriver": "awslogs",
          "options": {
            "awslogs-group": "${CLOUDWATCH_GROUP}",
            "awslogs-region": "${REGION}",
            "awslogs-stream-prefix": "ecs"
          }
        },
      "environment":[
         {
            "name":"DB_USER",
            "value":"${DB_USER}"
         },
         {
            "name":"DB_PASSWORD",
            "value":"${DB_PASSWORD}"
         },
         {
            "name":"POSTGRES_ENDPOINT",
            "value":"${POSTGRES_ENDPOINT}"
         },
         {
            "name":"POSTGRES_DATABASE",
            "value":"${POSTGRES_DATABASE}"
         },
         {
             "name":"JWT_GENERATOR_SIGNATURE_SECRET",
             "value":"${JWT_GENERATOR_SIGNATURE_SECRET}"
         }
      ]
   }
]
