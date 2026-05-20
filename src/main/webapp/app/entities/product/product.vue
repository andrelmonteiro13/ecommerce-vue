<template>
  <div>
    <h2 id="page-heading" data-cy="ProductHeading">
      <span id="product">{{ t$('ecommerceApp.product.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.product.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'ProductCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-product"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.product.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && products?.length === 0">
      <span>{{ t$('ecommerceApp.product.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="products?.length > 0">
      <table class="table table-striped" aria-describedby="products">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('name')">
              <span>{{ t$('ecommerceApp.product.name') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('slug')">
              <span>{{ t$('ecommerceApp.product.slug') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'slug'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('description')">
              <span>{{ t$('ecommerceApp.product.description') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('price')">
              <span>{{ t$('ecommerceApp.product.price') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'price'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('promotionalPrice')">
              <span>{{ t$('ecommerceApp.product.promotionalPrice') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'promotionalPrice'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('sku')">
              <span>{{ t$('ecommerceApp.product.sku') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'sku'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('imageUrl')">
              <span>{{ t$('ecommerceApp.product.imageUrl') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'imageUrl'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('stock')">
              <span>{{ t$('ecommerceApp.product.stock') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'stock'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('active')">
              <span>{{ t$('ecommerceApp.product.active') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'active'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('createdDate')">
              <span>{{ t$('ecommerceApp.product.createdDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('updatedDate')">
              <span>{{ t$('ecommerceApp.product.updatedDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('category.name')">
              <span>{{ t$('ecommerceApp.product.category') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'category.name'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ProductView', params: { productId: product.id } }">{{ product.id }}</router-link>
            </td>
            <td>{{ product.name }}</td>
            <td>{{ product.slug }}</td>
            <td>{{ product.description }}</td>
            <td>{{ product.price }}</td>
            <td>{{ product.promotionalPrice }}</td>
            <td>{{ product.sku }}</td>
            <td>{{ product.imageUrl }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.active }}</td>
            <td>{{ formatDateShort(product.createdDate) || '' }}</td>
            <td>{{ formatDateShort(product.updatedDate) || '' }}</td>
            <td>
              <div v-if="product.category">
                <router-link :to="{ name: 'CategoryView', params: { categoryId: product.category.id } }">{{
                  product.category.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'ProductView', params: { productId: product.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ProductEdit', params: { productId: product.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(product)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">{{ t$('entity.action.delete') }}</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #title>
        <span id="ecommerceApp.product.delete.question" data-cy="productDeleteDialogHeading">{{ t$('entity.delete.title') }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-product-heading">{{ t$('ecommerceApp.product.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-product"
            data-cy="entityConfirmDeleteButton"
            @click="removeProduct"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="products?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product.component.ts"></script>
