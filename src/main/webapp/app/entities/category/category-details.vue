<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="category">
        <h2 class="jh-entity-heading" data-cy="categoryDetailsHeading">
          <span>{{ t$('ecommerceApp.category.detail.title') }}Category</span> {{ category.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.category.name') }}</span>
          </dt>
          <dd>
            <span>{{ category.name }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.category.description') }}</span>
          </dt>
          <dd>
            <span>{{ category.description }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.category.active') }}</span>
          </dt>
          <dd>
            <span>{{ category.active }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link v-if="category.id" :to="{ name: 'CategoryEdit', params: { categoryId: category.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./category-details.component.ts"></script>
