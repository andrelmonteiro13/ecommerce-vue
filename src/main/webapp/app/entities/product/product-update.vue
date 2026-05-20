<template>
  <div class="product-page container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1 class="page-title">Cadastro de Produto</h1>
        <div class="breadcrumb-mini">Produtos > Novo Cadastro</div>
      </div>

      <div class="d-flex gap-2">
        <button class="btn btn-light" @click="previousState">Cancelar</button>

        <button class="btn btn-primary" :disabled="isSaving" @click="save">Salvar Produto</button>
      </div>
    </div>

    <div class="row g-4">
      <!-- COLUNA ESQUERDA -->
      <div class="col-lg-8">
        <div class="modern-card mb-4">
          <div class="modern-card-header">Dados Principais</div>

          <div class="modern-card-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Nome do Produto</label>

                <input v-model="product.name" type="text" class="form-control" />
              </div>

              <div class="col-md-6">
                <label class="form-label">SKU</label>

                <input v-model="product.sku" type="text" class="form-control" />
              </div>

              <div class="col-12">
                <label class="form-label">Slug</label>

                <input v-model="product.slug" type="text" class="form-control" />
              </div>

              <div class="col-12">
                <label class="form-label">Descrição</label>

                <textarea v-model="product.description" class="form-control"></textarea>
              </div>
            </div>
          </div>
        </div>

        <div class="modern-card">
          <div class="modern-card-header">Preços e Estoque</div>

          <div class="modern-card-body">
            <div class="row g-3">
              <div class="col-md-4">
                <label class="form-label">Preço</label>

                <input v-model="product.price" type="number" class="form-control" />
              </div>

              <div class="col-md-4">
                <label class="form-label">Preço Promocional</label>

                <input v-model="product.promotionalPrice" type="number" class="form-control" />
              </div>

              <div class="col-md-4">
                <label class="form-label">Estoque</label>

                <input v-model="product.stock" type="number" class="form-control" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- COLUNA DIREITA -->
      <div class="col-lg-4">
        <div class="modern-card mb-4">
          <div class="modern-card-header">Mídia do Produto</div>

          <div class="modern-card-body">
            <div class="product-image-preview mb-3">
              <img v-if="imageUrl" :src="imageUrl" alt="Imagem do produto" />

              <div v-else class="empty-image">Sem imagem</div>
            </div>

            <div class="upload-box">
              <input type="file" accept="image/*" class="form-control" @change="onFileChange" />
            </div>
          </div>
        </div>

        <div class="modern-card">
          <div class="modern-card-header">Categorização e Status</div>

          <div class="modern-card-body">
            <div class="mb-3">
              <label class="form-label">Categoria</label>

              <select v-model="product.category" class="form-select">
                <option v-for="category in categories" :key="category.id" :value="category">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="form-check form-switch mb-4">
              <input id="active" v-model="product.active" class="form-check-input" type="checkbox" />

              <label class="form-check-label" for="active"> Produto Ativo </label>
            </div>

            <div class="date-box">
              <div>
                <strong>Criado em:</strong>
                {{ convertDateTimeFromServer(product.createdDate) }}
              </div>

              <div>
                <strong>Atualizado em:</strong>
                {{ convertDateTimeFromServer(product.updatedDate) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product-update.component.ts"></script>

<style scoped>
.product-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
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
}

.modern-card-header {
  background: #f8fafc;
  padding: 18px 22px;
  font-weight: 700;
  border-bottom: 1px solid #e2e8f0;
}

.modern-card-body {
  padding: 24px;
}

.form-control,
.form-select {
  min-height: 48px;
  border-radius: 12px;
}

textarea.form-control {
  min-height: 140px;
}

.product-image-preview {
  width: 100%;
  height: 280px;
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

.upload-box {
  border: 2px dashed #cbd5e1;
  border-radius: 14px;
  padding: 18px;
  background: #fff;
}

.date-box {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  font-size: 0.9rem;
}

.btn-primary {
  border-radius: 12px;
  padding: 10px 22px;
}

.btn-light {
  border-radius: 12px;
  padding: 10px 22px;
}
</style>
