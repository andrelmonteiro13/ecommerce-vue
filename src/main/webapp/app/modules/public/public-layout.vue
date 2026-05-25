<template>
  <div class="public-site">
    <header class="site-header">
      <div class="site-header-inner">
        <router-link to="/site" class="logo-box">
          <img src="/content/images/logo-padaria-uniao.png" alt="Padaria União" />
        </router-link>

        <nav class="site-nav">
          <router-link to="/site">Início</router-link>
          <router-link to="/site/produtos"> Produtos </router-link>
          <router-link to="/site/nossa-historia">Nossa História</router-link>
          <a href="/site#localizacao">Localização</a>
          <a href="/site#contato">Contato</a>
        </nav>

        <div class="site-actions">
          <a class="whatsapp-btn" href="https://wa.me/557932322876" target="_blank"> Pedir no WhatsApp </a>
        </div>
      </div>
    </header>

    <main>
      <router-view />
    </main>

    <footer class="site-footer" id="contato">
      <div class="footer-grid">
        <div>
          <img class="footer-logo" src="/content/images/logo-padaria-uniao.png" alt="Padaria União" />
          <p>Preservando a tradição artesanal da panificação sergipana desde 1982.</p>
        </div>

        <div>
          <h4>Links Rápidos</h4>
          <router-link to="/site">Início</router-link>
          <router-link to="/site/nossa-historia">Nossa História</router-link>
          <a href="/site#produtos">Produtos</a>
        </div>

        <div>
          <h4>Atendimento</h4>
          <p>Rua Dona Abigail F. Araújo Ramos, 410 - Luzia</p>
          <p>(79) 3232-2876</p>
          <p>panificacao.uniao@yahoo.com.br</p>
        </div>
      </div>

      <div class="footer-copy">© 2026 Padaria União. A união de gerações desde 1982.</div>
    </footer>
  </div>
</template>

<style scoped>
.public-site {
  background: #fdfcfb;
  color: #191c20;
  font-family: Manrope, Arial, sans-serif;
}

.site-header {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 50;
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(12px);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.site-header-inner {
  max-width: 1280px;
  height: 78px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-box img {
  height: 48px;
}

.site-nav {
  display: flex;
  gap: 30px;
  font-family: Georgia, serif;
  font-size: 17px;
}

.site-nav a {
  color: #4b5563;
  text-decoration: none;
  padding-bottom: 6px;
}

.site-nav a.router-link-active,
.site-nav a:hover {
  color: #00457b;
  border-bottom: 2px solid #00457b;
}

.whatsapp-btn {
  background: #cc8b29;
  color: white;
  padding: 11px 18px;
  border-radius: 8px;
  font-weight: 700;
  text-decoration: none;
}

.site-footer {
  background: #00457b;
  color: white;
  padding: 70px 32px 24px;
}

.footer-grid {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr;
  gap: 48px;
}

.footer-logo {
  height: 58px;
  filter: brightness(0) invert(1);
  margin-bottom: 22px;
}

.site-footer a,
.site-footer p {
  display: block;
  color: #dbeafe;
  text-decoration: none;
  margin-bottom: 10px;
}

.site-footer h4 {
  font-family: Georgia, serif;
  margin-bottom: 18px;
}

.footer-copy {
  max-width: 1180px;
  margin: 50px auto 0;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  text-align: center;
  color: #bfdbfe;
  font-size: 13px;
}

@media (max-width: 900px) {
  .site-nav {
    display: none;
  }

  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
