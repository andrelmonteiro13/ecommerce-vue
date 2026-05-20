<template>
  <div class="product-view-page container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1 class="page-title">{{ product.name || 'Produto' }}</h1>
        <div class="breadcrumb-mini">Produtos &gt; Visualização</div>
      </div>

      <div class="d-flex gap-2">
        <button type="button" class="btn btn-light" @click="previousState()">Voltar</button>

        <router-link :to="{ name: 'ProductEdit', params: { productId: product.id } }" custom v-slot="{ navigate }">
          <button type="button" class="btn btn-primary" @click="navigate">Editar Produto</button>
        </router-link>
      </div>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <div class="modern-card mb-4">
          <div class="modern-card-header">Dados Principais</div>

          <div class="modern-card-body">
            <div class="info-grid">
              <div>
                <span>Código</span>
                <strong>{{ product.id }}</strong>
              </div>

              <div>
                <span>Nome</span>
                <strong>{{ product.name }}</strong>
              </div>

              <div>
                <span>Slug</span>
                <strong>{{ product.slug }}</strong>
              </div>

              <div>
                <span>SKU</span>
                <strong>{{ product.sku }}</strong>
              </div>

              <div>
                <span>Categoria</span>
                <strong>{{ product.category?.name }}</strong>
              </div>
            </div>

            <div class="mt-4">
              <span class="label">Descrição</span>
              <p class="description">{{ product.description || 'Sem descrição cadastrada.' }}</p>
            </div>
          </div>
        </div>

        <div class="modern-card">
          <div class="modern-card-header">Preços, Estoque e Status</div>

          <div class="modern-card-body">
            <div class="info-grid">
              <div>
                <span>Preço</span>
                <strong>R$ {{ product.price }}</strong>
              </div>

              <div>
                <span>Preço Promocional</span>
                <strong>R$ {{ product.promotionalPrice }}</strong>
              </div>

              <div>
                <span>Estoque</span>
                <strong>{{ product.stock }}</strong>
              </div>

              <div>
                <span>Status</span>
                <strong>
                  <span class="badge" :class="product.active ? 'bg-success' : 'bg-danger'">
                    {{ product.active ? 'Ativo' : 'Inativo' }}
                  </span>
                </strong>
              </div>

              <div>
                <span>Criado em</span>
                <strong>{{ formatDateLong(product.createdDate) }}</strong>
              </div>

              <div>
                <span>Atualizado em</span>
                <strong>{{ formatDateLong(product.updatedDate) }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="modern-card">
          <div class="modern-card-header">Imagem do Produto</div>

          <div class="modern-card-body">
            <div class="product-image-preview">
              <img v-if="product.imageUrl" :src="product.imageUrl" alt="Imagem do produto" />
              <div v-else class="empty-image">Sem imagem</div>
            </div>

            <div class="image-url mt-3" v-if="product.imageUrl">
              {{ product.imageUrl }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product-details.component.ts"></script>

<style scoped>
.product-view-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 4px;
}

.breadcrumb-mini {
  color: #64748b;
  font-size: 0.9rem;
}

.modern-card {
  background: #fff;
  border-radius: 18px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.04);
}

.modern-card-header {
  background: #f8fafc;
  padding: 18px 22px;
  font-weight: 700;
  border-bottom: 1px solid #e2e8f0;
  color: #0f172a;
}

.modern-card-body {
  padding: 24px;
}

.product-image-preview {
  width: 100%;
  height: 320px;
  border-radius: 18px;
  overflow: hidden;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.empty-image {
  color: #94a3b8;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.info-grid span,
.label {
  display: block;
  color: #64748b;
  font-size: 0.85rem;
  margin-bottom: 4px;
}

.info-grid strong {
  color: #0f172a;
  font-size: 1rem;
}

.description {
  margin-top: 8px;
  color: #334155;
  line-height: 1.6;
}

.image-url {
  font-size: 0.75rem;
  color: #64748b;
  word-break: break-all;
  background: #f8fafc;
  border-radius: 10px;
  padding: 10px;
}

.btn-primary,
.btn-light {
  border-radius: 12px;
  padding: 10px 22px;
}
</style>
