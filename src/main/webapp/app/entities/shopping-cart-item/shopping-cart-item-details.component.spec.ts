import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ShoppingCartItemDetails from './shopping-cart-item-details.vue';
import ShoppingCartItemService from './shopping-cart-item.service';

type ShoppingCartItemDetailsComponentType = InstanceType<typeof ShoppingCartItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shoppingCartItemSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShoppingCartItem Management Detail Component', () => {
    let shoppingCartItemServiceStub: SinonStubbedInstance<ShoppingCartItemService>;
    let mountOptions: MountingOptions<ShoppingCartItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shoppingCartItemServiceStub = sinon.createStubInstance<ShoppingCartItemService>(ShoppingCartItemService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          shoppingCartItemService: () => shoppingCartItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shoppingCartItemServiceStub.find.resolves(shoppingCartItemSample);
        route = {
          params: {
            shoppingCartItemId: `${123}`,
          },
        };
        const wrapper = shallowMount(ShoppingCartItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shoppingCartItem).toMatchObject(shoppingCartItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shoppingCartItemServiceStub.find.resolves(shoppingCartItemSample);
        const wrapper = shallowMount(ShoppingCartItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
