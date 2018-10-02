package uk.gov.ons.fwmt.fwmtrmadapter.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

class ConnectionFactoryBuilder {

  static CachingConnectionFactory createConnectionFactory(int port, String hostname, String virtualHost,
      String password, String username) {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();

    cachingConnectionFactory.setPort(port);
    cachingConnectionFactory.setHost(hostname);
    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }
}
