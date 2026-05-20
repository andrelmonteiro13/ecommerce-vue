import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ProductService from '@/entities/product/product.service';
import ShoppingCartService from '@/entities/shopping-cart/shopping-cart.service';
import AlertService from '@/shared/alert/alert.service';

import ShoppingCartItemUpdate from './shopping-cart-item-update.vue';
import ShoppingCartItemService from './shopping-cart-item.service';

type ShoppingCartItemUpdateComponentType = InstanceType<typeof ShoppingCartItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shoppingCartItemSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShoppingCartItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShoppingCartItem Management Update Component', () => {
    let comp: ShoppingCartItemUpdateComponentType;
    let shoppingCartItemServiceStub: SinonStubbedInstance<ShoppingCartItemService>;

    beforeEach(() => {
      route = {};
      shoppingCartItemServiceStub = sinon.createStubInstance<ShoppingCartItemService>(ShoppingCartItemService);
      shoppingCartItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          shoppingCartItemService: () => shoppingCartItemServiceStub,
          shoppingCartService: () =>
            sinon.createStubInstance<ShoppingCartService>(ShoppingCartService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          productService: () =>
            sinon.createStubInstance<ProductService>(ProductService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ShoppingCartItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shoppingCartItem = shoppingCartItemSample;
        shoppingCartItemServiceStub.update.resolves(shoppingCartItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shoppingCartItemServiceStub.update.calledWith(shoppingCartItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shoppingCartItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShoppingCartItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shoppingCartItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shoppingCartItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        shoppingCartItemServiceStub.find.resolves(shoppingCartItemSample);
        shoppingCartItemServiceStub.retrieve.resolves([shoppingCartItemSample]);

        // WHEN
        route = {
          params: {
            shoppingCartItemId: `${shoppingCartItemSample.id}`,
          },
        };
        const wrapper = shallowMount(ShoppingCartItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shoppingCartItem).toMatchObject(shoppingCartItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shoppingCartItemServiceStub.find.resolves(shoppingCartItemSample);
        const wrapper = shallowMount(ShoppingCartItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
