<template>
  <div class="product-detail-page">
    <section class="detail-hero">
      <div class="detail-container">
        <div class="breadcrumb-area">
          <router-link to="/site/produtos" class="back-link">← Voltar para produtos</router-link>

          <span class="category-badge">
            {{ product?.category?.name || 'Produto artesanal' }}
          </span>
        </div>

        <h1>{{ product?.name || 'Produto' }}</h1>

        <p class="hero-description">
          {{ product?.description || 'Produto artesanal preparado com cuidado e tradição.' }}
        </p>
      </div>
    </section>

    <section v-if="product" class="product-detail-content">
      <div class="content-grid">
        <div class="image-card">
          <img :src="productImage" :alt="product.name" @error="onImageError" />
        </div>

        <div class="info-card">
          <span class="category-label">
            {{ product.category?.name || 'PADARIA' }}
          </span>

          <h2>{{ product.name }}</h2>

          <div class="price">
            {{ formatCurrency(product.promotionalPrice || product.price) }}
          </div>

          <div class="description-box">
            <h3>Descrição</h3>
            <p>{{ product.description || 'Produto artesanal preparado com ingredientes selecionados.' }}</p>
          </div>

          <div class="meta-grid">
            <div class="meta-item">
              <span>SKU</span>
              <strong>{{ product.sku || 'N/A' }}</strong>
            </div>

            <div class="meta-item">
              <span>Disponibilidade</span>
              <strong>{{ product.stock && product.stock > 0 ? 'Disponível' : 'Sob encomenda' }}</strong>
            </div>
          </div>

          <div class="actions">
            <a :href="whatsappLink" target="_blank" class="whatsapp-btn">Pedir no WhatsApp</a>

            <router-link to="/site/produtos" class="secondary-btn">Ver mais produtos</router-link>
          </div>
        </div>
      </div>
    </section>

    <section v-else-if="!isLoading" class="not-found">
      <h2>Produto não encontrado</h2>
      <router-link to="/site/produtos" class="secondary-btn">Voltar para produtos</router-link>
    </section>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

export default defineComponent({
  name: 'PublicProductDetail',

  setup() {
    const route = useRoute();

    const product = ref<any>(null);
    const isLoading = ref(true);
    const imageErrorHandled = ref(false);

    const fallbackImage = new URL('../../../content/images/padaria-interna.jpg', import.meta.url).href;

    const loadProduct = async () => {
      try {
        const slug = route.params.slug as string;

        const response = await fetch(`${window.location.origin}/api/public/products/${slug}`, {
          method: 'GET',
          headers: {
            Accept: 'application/json',
          },
        });

        if (!response.ok) {
          product.value = null;
          return;
        }

        product.value = await response.json();
      } catch (error) {
        console.error(error);
        product.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    const productImage = computed(() => {
      const imageUrl = product.value?.imageUrl;

      if (imageUrl && imageUrl.startsWith('http')) {
        return imageUrl;
      }

      return fallbackImage;
    });

    const onImageError = (event: Event) => {
      if (imageErrorHandled.value) {
        return;
      }

      imageErrorHandled.value = true;
      (event.target as HTMLImageElement).src = fallbackImage;
    };

    const formatCurrency = (value?: number | null) => {
      if (value === null || value === undefined) {
        return 'Preço sob consulta';
      }

      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
      }).format(value);
    };

    const whatsappLink = computed(() => {
      const message = `Olá! Gostaria de pedir o produto: ${product.value?.name || ''}`;
      return `https://wa.me/557932322876?text=${encodeURIComponent(message)}`;
    });

    onMounted(loadProduct);

    return {
      product,
      isLoading,
      productImage,
      whatsappLink,
      formatCurrency,
      onImageError,
    };
  },
});
</script>

<style scoped>
.product-detail-page {
  background: #f7f4ef;
  min-height: 100vh;
}

.detail-hero {
  background: linear-gradient(135deg, #0c4c86 0%, #2867a7 100%);
  color: white;
  padding: 46px 0 72px;
}

.detail-container {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 24px;
}

.breadcrumb-area {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.back-link {
  color: white;
  text-decoration: none;
  font-weight: 700;
}

.category-badge {
  background: rgba(255, 193, 7, 0.15);
  color: #ffc107;
  border: 1px solid rgba(255, 193, 7, 0.45);
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.detail-hero h1 {
  font-family: Georgia, serif;
  font-size: clamp(3rem, 6vw, 5.4rem);
  line-height: 0.95;
  font-weight: 800;
  max-width: 760px;
  margin: 0 0 22px;
}

.hero-description {
  max-width: 720px;
  font-size: 18px;
  line-height: 1.7;
  opacity: 0.95;
  margin: 0;
}

.product-detail-content {
  max-width: 1180px;
  margin: 64px auto 0;
  padding: 0 24px 90px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  gap: 34px;
  align-items: start;
}

.image-card {
  background: white;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.1);
}

.image-card img {
  width: 100%;
  height: 560px;
  object-fit: cover;
  display: block;
}

.info-card {
  background: white;
  border-radius: 28px;
  padding: 36px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.1);
}

.category-label {
  display: inline-block;
  margin-bottom: 18px;
  color: #d79a24;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.info-card h2 {
  font-family: Georgia, serif;
  font-size: clamp(2.4rem, 4vw, 4rem);
  line-height: 1;
  color: #0c4c86;
  margin: 0 0 20px;
}

.price {
  font-size: 38px;
  font-weight: 800;
  color: #d79a24;
  margin-bottom: 30px;
}

.description-box {
  background: #f7f8fb;
  border-radius: 18px;
  padding: 24px;
  margin-bottom: 24px;
}

.description-box h3 {
  color: #0c4c86;
  margin: 0 0 12px;
}

.description-box p {
  line-height: 1.7;
  color: #4f6580;
  margin: 0;
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 30px;
}

.meta-item {
  background: #f7f8fb;
  border-radius: 16px;
  padding: 18px;
}

.meta-item span {
  display: block;
  color: #7b8ba1;
  font-size: 13px;
  margin-bottom: 6px;
}

.meta-item strong {
  color: #0c4c86;
}

.actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.whatsapp-btn,
.secondary-btn {
  text-decoration: none;
  padding: 15px 22px;
  border-radius: 14px;
  font-weight: 800;
}

.whatsapp-btn {
  background: #0c4c86;
  color: white;
}

.secondary-btn {
  background: #edf2f7;
  color: #0c4c86;
}

.not-found {
  padding: 120px 24px;
  text-align: center;
}

.not-found h2 {
  color: #0c4c86;
  font-family: Georgia, serif;
  margin-bottom: 24px;
}

@media (max-width: 992px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .image-card img {
    height: auto;
  }
}

@media (max-width: 576px) {
  .detail-hero {
    padding: 34px 0 56px;
  }

  .product-detail-content {
    margin-top: 36px;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .whatsapp-btn,
  .secondary-btn {
    width: 100%;
    text-align: center;
  }
}
</style>
