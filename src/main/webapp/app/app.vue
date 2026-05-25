<template>
  <BToastOrchestrator />
  <div id="app">
    <ribbon></ribbon>
    <div id="app-header">
      <jhi-navbar v-if="!$route.path.startsWith('/site')"></jhi-navbar>
    </div>
    <div class="container-fluid">
      <div class="card jh-card">
        <router-view></router-view>
      </div>
      <b-modal id="login-page" focus="username" v-model="loginModalOpen" :no-footer="true" lazy>
        <template #title>
          <span data-cy="loginTitle" id="login-title">{{ t$('login.title') }}</span>
        </template>
        <login-form v-if="loginModalOpen"></login-form>
      </b-modal>
      <jhi-footer v-if="!$route.path.startsWith('/site')"></jhi-footer>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>
