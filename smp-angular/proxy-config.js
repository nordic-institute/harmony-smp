const PROXY_CONFIG = {
  "/rest/search**": {
    target: "http://localhost:8080/smp/ui/",
    changeOrigin: true,

    secure: false,
    logLevel: "debug",
    // cookiePathRewrite: "/" // Doesn't work
    onProxyRes: function (proxyRes, req, res) {
      let cookies = proxyRes.headers["set-cookie"];
      if (cookies) {
        proxyRes.headers["set-cookie"] = cookies.map(cookie =>
          cookie.replace("path=/smp/", "path=/").replace("Path=/smp/", "Path=/").replace("//", "/"));
      }
    },
  }
};

