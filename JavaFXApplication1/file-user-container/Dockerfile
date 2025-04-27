FROM ubuntu:latest

# Install SSH server
RUN apt-get update && apt-get install -y openssh-server

# Create the necessary directories and set permissions
RUN mkdir -p /run/sshd && chmod 700 /run/sshd

RUN echo 'root:yourpassword' | chpasswd

# Generate SSH host keys if not present
RUN ssh-keygen -A

# Expose the SSH port
EXPOSE 22

# Start the SSH daemon
CMD ["/usr/sbin/sshd", "-D"]
