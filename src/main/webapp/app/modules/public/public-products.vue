<template>
  <div class="products-page">
    <section class="products-hero">
      <div>
        <span class="eyebrow">Produtos Artesanais</span>
        <h1>Vitrine da Padaria União</h1>
        <p>Escolha seus favoritos e peça direto pelo WhatsApp.</p>
      </div>
    </section>

    <section class="products-content">
      <div class="filters-card">
        <div class="search-box">
          <label>Buscar produto</label>
          <input v-model="searchTerm" type="text" placeholder="Ex: pão, bolo, torta..." />
        </div>

        <div class="category-box">
          <label>Categoria</label>
          <select v-model="selectedCategory">
            <option value="">Todas as categorias</option>
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>
      </div>

      <div v-if="isLoading" class="empty-state">Carregando produtos...</div>

      <div v-else-if="filteredProducts.length === 0" class="empty-state">Nenhum produto encontrado.</div>

      <div v-else class="products-grid">
        <article v-for="product in filteredProducts" :key="product.id" class="product-card">
          <div class="product-image">
            <img v-if="isValidImage(product.imageUrl)" :src="product.imageUrl" :alt="product.name" />
            <div v-else class="product-no-image">Sem imagem</div>
          </div>

          <div class="product-info">
            <span class="product-category">
              {{ product.category?.name || 'Produto artesanal' }}
            </span>

            <h3>{{ product.name }}</h3>
            <p>{{ shortDescription(product.description) }}</p>

            <div class="product-footer">
              <strong>{{ formatCurrency(product.promotionalPrice || product.price) }}</strong>
              <a :href="whatsappLink(product)" target="_blank" class="whatsapp-product">Pedir</a>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue';
import { type IProduct } from '@/shared/model/product.model';
import PublicProductService from './public-product.service';

export default defineComponent({
  name: 'PublicProducts',
  setup() {
    const productService = new PublicProductService();

    const products = ref<IProduct[]>([]);
    const categories = ref<string[]>([]);
    const isLoading = ref(true);
    const searchTerm = ref('');
    const selectedCategory = ref('');

    const loadProducts = async () => {
      try {
        const result = await productService.retrieve();
        products.value = result || [];
      } finally {
        isLoading.value = false;
      }
    };

    const loadCategories = async () => {
  categories.value = [
    ...new Set(
      products.value
        .map(product => product.category?.name)
        .filter((name): name is string => !!name && name.trim().length > 0)
    ),
  ].sort();
};

    const filteredProducts = computed(() => {
      const term = searchTerm.value.trim().toLowerCase();

      return products.value.filter(product => {
        const productName = product.name?.toLowerCase() || '';
        const productDescription = product.description?.toLowerCase() || '';
        const productCategory = product.category?.name || '';

        const matchesSearch = !term || productName.includes(term) || productDescription.includes(term);

        const matchesCategory = !selectedCategory.value || productCategory === selectedCategory.value;

        return matchesSearch && matchesCategory;
      });
    });

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

      return description.length > 110 ? `${description.substring(0, 110)}...` : description;
    };

    const isValidImage = (imageUrl?: string | null) => {
      return !!imageUrl && imageUrl.startsWith('http');
    };

    const whatsappLink = (product: IProduct) => {
      const message = `Olá! Gostaria de pedir o produto: ${product.name}`;
      return `https://wa.me/557932322876?text=${encodeURIComponent(message)}`;
    };

   onMounted(async () => {
  await loadProducts();
  await loadCategories();
});

    return {
      products,
      isLoading,
      searchTerm,
      selectedCategory,
      categories,
      filteredProducts,
      formatCurrency,
      shortDescription,
      isValidImage,
      whatsappLink,
    };
  },
});
</script>

<style scoped>
.products-page {
  background: #fdfcfb;
  color: #191c20;
}

.products-hero {
  min-height: 360px;
  padding: 150px 24px 70px;
  background: linear-gradient(135deg, rgba(0, 69, 123, 0.96), rgba(29, 93, 155, 0.9));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.products-hero > div {
  max-width: 780px;
}

.eyebrow {
  color: #cc8b29;
  font-weight: 800;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  font-size: 0.78rem;
}

.products-hero h1 {
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(3rem, 5vw, 5rem);
  font-weight: 800;
  margin: 18px 0;
}

.products-hero p {
  color: #dbeafe;
  font-size: 1.2rem;
}

.products-content {
  max-width: 1180px;
  margin: 0 auto;
  padding: 70px 24px 110px;
}

.filters-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 46px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 22px;
}

.filters-card label {
  display: block;
  color: #00457b;
  font-weight: 800;
  margin-bottom: 8px;
}

.filters-card input,
.filters-card select {
  width: 100%;
  min-height: 48px;
  border: 1px solid #dbe3ef;
  border-radius: 12px;
  padding: 0 14px;
  outline: none;
  background: #f8fafc;
}

.filters-card input:focus,
.filters-card select:focus {
  border-color: #cc8b29;
  box-shadow: 0 0 0 3px rgba(204, 139, 41, 0.14);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.product-card {
  background: white;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 60px rgba(15, 23, 42, 0.13);
}

.product-image {
  aspect-ratio: 1 / 1;
  background: #edf2f7;
  overflow: hidden;
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

.product-info {
  padding: 22px;
}

.product-category {
  display: block;
  color: #cc8b29;
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 10px;
}

.product-info h3 {
  font-family: Georgia, serif;
  font-size: 1.25rem;
  color: #00457b;
  margin-bottom: 10px;
}

.product-info p {
  color: #64748b;
  min-height: 68px;
  line-height: 1.55;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.product-footer strong {
  color: #cc8b29;
  font-size: 1.1rem;
}

.whatsapp-product {
  background: #00457b;
  color: white;
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 10px;
  font-weight: 800;
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: #64748b;
  font-size: 1.1rem;
}

@media (max-width: 1050px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 820px) {
  .filters-card,
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>