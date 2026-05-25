<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-overlay"></div>

      <div class="hero-content">
        <span class="eyebrow">Seja bem-vindo</span>
        <h1>Padaria com cheiro de pão</h1>
        <p>Tradição e sabor artesanal em Aracaju desde 1982. O verdadeiro pão quentinho que abraça a sua manhã.</p>

        <div class="hero-actions">
          <a href="#produtos" class="btn-primary-site">Explore Menu</a>
          <router-link to="/site/nossa-historia" class="btn-secondary-site">Nossa História</router-link>
        </div>
      </div>
    </section>

    <section class="fresh-section">
      <div class="section-title-row">
        <span class="icon-circle">⏱</span>
        <h2>Saído do Forno</h2>
      </div>

      <div class="fresh-grid">
        <article class="fresh-card highlighted">
          <div class="fresh-icon">⏱</div>
          <div>
            <h3>Pão Francês</h3>
            <strong>AGORA MESMO</strong>
            <p>Crocante e fumegante, pronto para o seu café.</p>
          </div>
        </article>

        <article class="fresh-card">
          <div class="fresh-icon">⏱</div>
          <div>
            <h3>Bolo de Rolo</h3>
            <strong>HÁ 15 MIN</strong>
            <p>Nossa receita tradicional pernambucana.</p>
          </div>
        </article>

        <article class="fresh-card">
          <div class="fresh-icon">⏱</div>
          <div>
            <h3>Focaccia Alecrim</h3>
            <strong>HÁ 45 MIN</strong>
            <p>Finalizada com azeite extra virgem e sal grosso.</p>
          </div>
        </article>
      </div>
    </section>

    <section class="heritage-section">
      <div class="heritage-image">
        <img :src="heritageImage" alt="Interior da Padaria União" />
      </div>

      <div class="heritage-content">
        <span class="eyebrow">Desde 1982</span>
        <h2>Nossa Herança</h2>
        <p>
          Fundada no coração de Aracaju, a Padaria União nasceu do sonho de trazer o pão artesanal de volta às mesas das famílias
          sergipanas. Há mais de 40 anos, mantemos viva a tradição da fermentação lenta e o cuidado em cada detalhe.
        </p>
        <blockquote>
          O segredo? Ingredientes selecionados e o tempo necessário para cada aroma se desenvolver. Por isso dizem: não é apenas uma
          padaria, é a Padaria com cheiro de pão.
        </blockquote>

        <router-link to="/site/nossa-historia" class="history-link"> Conheça mais nossa história → </router-link>
      </div>
    </section>

    <section id="produtos" class="products-section">
      <div class="section-heading-center">
        <h2>Favoritos Artesanais</h2>
        <div class="accent-line"></div>
      </div>

      <div v-if="isLoading" class="loading-message">Carregando produtos...</div>

      <div v-else-if="products.length === 0" class="loading-message">Nenhum produto ativo cadastrado.</div>

      <div v-else class="products-grid">
        <article v-for="product in featuredProducts" :key="product.id" class="product-card">
          <div class="product-image">
            <img v-if="isValidImage(product.imageUrl)" :src="product.imageUrl" :alt="product.name" />
            <div v-else class="product-no-image">Sem imagem</div>
          </div>

          <h3>{{ product.name }}</h3>
          <p>{{ shortDescription(product.description) }}</p>

          <strong class="product-price">
            {{ formatCurrency(product.promotionalPrice || product.price) }}
          </strong>
        </article>
      </div>

      <div class="products-action">
        <a href="#produtos" class="outline-button">Ver Menu Completo</a>
      </div>
    </section>

    <section id="localizacao" class="location-section">
      <div class="location-info">
        <h2>Visite-nos</h2>

        <div class="location-item">
          <span>📍</span>
          <div>
            <h4>Endereço</h4>
            <p>Rua Dona Abigail Ferreira Araújo Ramos, 410 - Luzia, Aracaju - SE</p>
          </div>
        </div>

        <div class="location-item">
          <span>🕒</span>
          <div>
            <h4>Horário de Funcionamento</h4>
            <p>Segunda a Sábado: 06:00 - 19:30</p>
          </div>
        </div>

        <div class="location-item">
          <span>☎</span>
          <div>
            <h4>Contato</h4>
            <p>(79) 3232-2876 | 3217-6519</p>
            <p>panificacao.uniao@yahoo.com.br</p>
          </div>
        </div>
      </div>

      <div class="map-container">
        <iframe
          src="https://maps.google.com/maps?width=100%25&height=600&hl=pt-BR&q=Rua%20Dona%20Abigail%20Ferreira%20Ara%C3%BAjo%20Ramos%20410%20Aracaju+(Padaria%20Uni%C3%A3o)&t=&z=16&ie=UTF8&iwloc=B&output=embed"
          frameborder="0"
          scrolling="no"
          marginheight="0"
          marginwidth="0"
          loading="lazy"
        >
        </iframe>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue';
import { type IProduct } from '@/shared/model/product.model';
import PublicProductService from './public-product.service';

export default defineComponent({
  name: 'PublicHome',
  setup() {
    const productService = new PublicProductService();
    const products = ref<IProduct[]>([]);
    const isLoading = ref(true);

    const heroImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;

    const heritageImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;

    const mapImage = 'https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&w=1200&q=80';

    const loadProducts = async () => {
      try {
        const result = await productService.retrieve();
        products.value = result.filter(product => product.active);
      } finally {
        isLoading.value = false;
      }
    };

    const featuredProducts = computed(() => products.value.slice(0, 4));

    const formatCurrency = (value?: number | null) => {
      if (value === null || value === undefined) {
        return 'Preço sob consulta';
      }

      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }).format(value);
    };

    const shortDescription = (description?: string | null) => {
      if (!description || description.includes('fake-data')) {
        return 'Produto artesanal preparado com cuidado e tradição.';
      }

      return description.length > 85 ? `${description.substring(0, 85)}...` : description;
    };

    const isValidImage = (imageUrl?: string | null) => {
      return !!imageUrl && imageUrl.startsWith('http');
    };

    onMounted(loadProducts);

    return {
      products,
      featuredProducts,
      isLoading,
      heroImage,
      heritageImage,
      mapImage,
      formatCurrency,
      shortDescription,
      isValidImage,
    };
  },
});
</script>

<style scoped>
.home-page {
  background: #fdfcfb;
  color: #191c20;
}

.hero-section {
  position: relative;
  min-height: 78vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 140px 24px 90px;
  background-image: url('https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=1800&q=80');
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(rgba(0, 69, 123, 0.68), rgba(0, 69, 123, 0.48));
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 850px;
  text-align: center;
  color: white;
}

.eyebrow {
  color: #cc8b29;
  font-weight: 800;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  font-size: 0.78rem;
}

.hero-content h1 {
  margin: 20px 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(3rem, 6vw, 6rem);
  line-height: 0.95;
  font-weight: 800;
  text-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.hero-content p {
  max-width: 720px;
  margin: 0 auto 32px;
  font-size: 1.25rem;
  font-style: italic;
  color: rgba(255, 255, 255, 0.92);
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary-site,
.btn-secondary-site {
  padding: 14px 28px;
  border-radius: 8px;
  font-weight: 800;
  text-decoration: none;
}

.btn-primary-site {
  background: #00457b;
  color: white;
}

.btn-secondary-site {
  background: rgba(255, 255, 255, 0.14);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(10px);
}

.fresh-section,
.products-section,
.location-section {
  max-width: 1180px;
  margin: 0 auto;
  padding: 96px 24px;
}

.section-title-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 40px;
}

.icon-circle {
  color: #cc8b29;
  font-size: 2rem;
}

.section-title-row h2,
.section-heading-center h2,
.location-info h2,
.heritage-content h2 {
  font-family: Georgia, 'Times New Roman', serif;
  color: #00457b;
  font-weight: 800;
}

.section-title-row h2 {
  font-size: 2.3rem;
}

.fresh-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 26px;
}

.fresh-card {
  display: flex;
  gap: 18px;
  background: rgba(0, 69, 123, 0.05);
  border-radius: 18px;
  padding: 28px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04);
}

.fresh-icon {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  background: rgba(0, 69, 123, 0.14);
  color: #00457b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fresh-card.highlighted .fresh-icon {
  background: #cc8b29;
  color: white;
}

.fresh-card h3 {
  font-family: Georgia, serif;
  font-size: 1.15rem;
  color: #191c20;
  margin-bottom: 4px;
}

.fresh-card strong {
  display: block;
  color: #cc8b29;
  font-size: 0.75rem;
  margin-bottom: 8px;
}

.fresh-card p {
  color: #64748b;
  margin: 0;
}

.heritage-section {
  background: #f6f3ef;
  padding: 110px 24px;
  display: grid;
  grid-template-columns: minmax(0, 520px) minmax(0, 520px);
  justify-content: center;
  gap: 80px;
  align-items: center;
}

.heritage-image {
  position: relative;
}

.heritage-image::before {
  content: '';
  position: absolute;
  inset: -16px;
  background: rgba(0, 69, 123, 0.1);
  border-radius: 24px;
  transform: rotate(3deg);
}

.heritage-image img {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 22px;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.18);
}

.heritage-content h2 {
  font-size: 3rem;
  margin: 16px 0 24px;
}

.heritage-content p,
.heritage-content blockquote {
  color: #64748b;
  font-size: 1.08rem;
  line-height: 1.75;
}

.heritage-content blockquote {
  border-left: 4px solid #cc8b29;
  padding-left: 20px;
  font-style: italic;
}

.history-link {
  display: inline-block;
  color: #00457b;
  font-weight: 800;
  text-decoration: none;
  margin-top: 10px;
}

.section-heading-center {
  text-align: center;
  margin-bottom: 58px;
}

.section-heading-center h2 {
  font-size: 2.7rem;
}

.accent-line {
  width: 90px;
  height: 4px;
  background: #cc8b29;
  border-radius: 99px;
  margin: 18px auto 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
}

.product-card {
  cursor: default;
}

.product-image {
  aspect-ratio: 1 / 1;
  overflow: hidden;
  border-radius: 18px;
  margin-bottom: 20px;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.12);
  background: #edf2f7;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.product-no-image {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.product-card h3 {
  font-family: Georgia, serif;
  color: #191c20;
  font-size: 1.18rem;
  margin-bottom: 8px;
}

.product-card p {
  color: #64748b;
  font-size: 0.92rem;
  min-height: 44px;
}

.product-price {
  color: #cc8b29;
  font-size: 1.05rem;
}

.products-action {
  text-align: center;
  margin-top: 56px;
}

.outline-button {
  display: inline-block;
  border: 2px solid #cc8b29;
  color: #cc8b29;
  padding: 13px 34px;
  border-radius: 10px;
  font-weight: 800;
  text-decoration: none;
}

.location-section {
  display: grid;
  grid-template-columns: 1fr 1.1fr;
  gap: 80px;
  align-items: center;
}

.location-info h2 {
  font-size: 2.7rem;
  margin-bottom: 40px;
}

.location-item {
  display: flex;
  gap: 18px;
  margin-bottom: 30px;
}

.location-item span {
  color: #cc8b29;
  font-size: 1.7rem;
}

.location-item h4 {
  color: #191c20;
  font-weight: 800;
  margin-bottom: 6px;
}

.location-item p {
  margin: 0 0 4px;
  color: #64748b;
}

.map-card {
  height: 430px;
  overflow: hidden;
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.14);
}

.map-container {
  width: 100%;
  min-height: 520px;
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.12);
}

.map-container iframe {
  width: 100%;
  height: 520px;
  border: 0;
  display: block;
}

@media (max-width: 980px) {
  .fresh-grid,
  .products-grid,
  .heritage-section,
  .location-section {
    grid-template-columns: 1fr;
  }

  .hero-content h1 {
    font-size: 3.2rem;
  }

  .heritage-section {
    gap: 48px;
  }
}
</style>
