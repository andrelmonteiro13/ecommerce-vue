export default [
  {
    path: '/site',
    component: () => import('@/modules/public/public-layout.vue'),
    children: [
      {
        path: '',
        name: 'PublicHome',
        component: () => import('@/modules/public/public-home.vue'),
      },
      {
        path: 'produtos',
        name: 'PublicProducts',
        component: () => import('@/modules/public/public-products.vue'),
      },
      {
        path: 'produto/:slug',
        name: 'PublicProductDetail',
        component: () => import('@/modules/public/public-product-detail.vue'),
      },
      {
        path: 'nossa-historia',
        name: 'PublicHistory',
        component: () => import('@/modules/public/public-history.vue'),
      },
    ],
  },
];
