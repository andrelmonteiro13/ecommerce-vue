import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CategoryService from '@/entities/category/category.service';
import { uploadProductImage } from '@/services/product-upload.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import useDataUtils from '@/shared/data/data-utils.service';
import { type ICategory } from '@/shared/model/category.model';
import { type IProduct, Product } from '@/shared/model/product.model';

import ProductService from './product.service';

export default defineComponent({
  name: 'ProductUpdate',
  setup() {
    const productService = inject('productService', () => new ProductService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const product: Ref<IProduct> = ref(new Product());

    const categoryService = inject('categoryService', () => new CategoryService());

    const categories: Ref<ICategory[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const selectedImageFile = ref<File | null>(null);
    const imageUrl = ref<string | null>(null);

    const onFileChange = (event: Event) => {
      const input = event.target as HTMLInputElement;
      selectedImageFile.value = input.files?.[0] ?? null;

      if (selectedImageFile.value) {
        imageUrl.value = URL.createObjectURL(selectedImageFile.value);
      }
    };

    const uploadImage = async (productId: number) => {
      if (!selectedImageFile.value) {
        return null;
      }

      const updatedProduct = await uploadProductImage(productId, selectedImageFile.value);
      imageUrl.value = updatedProduct.imageUrl;
      return updatedProduct;
    };

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProduct = async productId => {
      try {
        const res = await productService().find(productId);
        res.createdDate = new Date(res.createdDate);
        res.updatedDate = new Date(res.updatedDate);
        product.value = res;
        imageUrl.value = res.imageUrl ?? null;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.productId) {
      retrieveProduct(route.params.productId);
    }

    const initRelationships = () => {
      categoryService()
        .retrieve({
          page: 0,
          size: 9999,
          sort: ['name,asc'],
        })
        .then(res => {
          categories.value = res.data;
        });
    };
    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 3 }).toString(), 3),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 150 }).toString(), 150),
      },
      slug: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      price: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      promotionalPrice: {
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      sku: {},
      imageUrl: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 500 }).toString(), 500),
      },
      stock: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      active: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdDate: {},
      updatedDate: {},
      category: {},
    };

    const v$ = useVuelidate(validationRules, product as any);
    v$.value.$validate();

    return {
      productService,
      alertService,
      product,
      previousState,
      isSaving,
      currentLanguage,
      categories,
      selectedImageFile,
      imageUrl,
      onFileChange,
      uploadImage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: product }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;

      if (this.product.id) {
        this.productService()
          .update(this.product)
          .then(async param => {
            if (this.selectedImageFile) {
              await this.uploadImage(param.id);
            }

            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.product.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.productService()
          .create(this.product)
          .then(async param => {
            if (this.selectedImageFile) {
              await this.uploadImage(param.id);
            }

            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.product.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
