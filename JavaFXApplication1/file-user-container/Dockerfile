FROM ubuntu:24.04

# Install SSH server
RUN apt-get update && apt-get install -y openssh-server nano

# Set root password (you can choose your own password here)
RUN echo "root:rootpassword" | chpasswd

# Allow root login and password authentication by updating sshd_config
RUN sed -i 's/#\?PermitRootLogin .*/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#\?PasswordAuthentication .*/PasswordAuthentication yes/' /etc/ssh/sshd_config && \
    sed -i 's/#\?KbdInteractiveAuthentication .*/KbdInteractiveAuthentication no/' /etc/ssh/sshd_config

# Create SSH directory and start the SSH service
RUN mkdir /var/run/sshd

# Expose SSH port
EXPOSE 22

# Start SSH server
CMD ["/usr/sbin/sshd", "-D"]
