FROM python:3.9.13

# Set the working directory in the container
WORKDIR /app

# Copy the requirements.txt file to the container
COPY requirements.txt ./

# Install the application dependencies
RUN pip install -r requirements.txt

# Copy the application code to the container
COPY . .

# Specify the command to run when the container starts
CMD [ "python", "./manage.py", "runserver", "0.0.0.0:8000" ]

EXPOSE 8000