FROM httpd:2.4.39

# in order to be able to use additional terminal capabilities
ENV TERM=xterm-256color

# turn off the package manager interactive mode during the image build
ENV DEBIAN_FRONTEND noninteractive

# Set the correct timezone
ENV TZ=Europe/Brussels
RUN ln -sf /usr/share/zoneinfo/"${TZ}" /etc/localtime && \
    echo "${TZ}" > /etc/timezone

# install some useful packages
RUN apt-get update && apt-get install -y unzip net-tools vim wget && \
# activate vim syntax highlight
    sed -i 's/"syntax on/syntax on/g' /etc/vim/vimrc && \
# create apache httpd vhosts directory
    mkdir /usr/local/apache2/conf/vhosts.d

# add configuration files and other related files
COPY resources/httpd-default.conf /usr/local/apache2/conf/extra/
COPY resources/httpd.conf /usr/local/apache2/conf/
COPY resources/vhost_corner.conf /usr/local/apache2/conf/vhosts.d/
COPY resources/entrypoint.sh /usr/local/bin/

# set the correct permissions for the copied files
RUN chown -R root:www-data /usr/local/apache2/conf/ \
    && chmod +x /usr/local/bin/entrypoint.sh

# turn off the package manager interactive mode during the image build
ENV DEBIAN_FRONTEND noninteractive

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

CMD ["httpd-foreground"]